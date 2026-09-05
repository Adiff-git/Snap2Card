package com.snap2card.feature.snap2card.presentation.review

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.snap2card.domain.service.OcrTextProcessor
import com.snap2card.feature.snap2card.domain.usecase.ExtractTextForOcrUseCase
import com.snap2card.feature.snap2card.domain.usecase.GenerateCardsFromOcrTextUseCase
import com.snap2card.feature.snap2card.domain.vocabulary.repository.GeneratedVocabularyCardStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenerationSource(
    val type: String,
    val uri: Uri,
    val mimeType: String,
    val name: String?,
) {
    val displayName: String = name?.takeIf { it.isNotBlank() }
        ?: if (type == "camera") "Captured photo" else "Selected file"
}

sealed class CardGenerationInputUiState {
    data class Loading(
        val source: GenerationSource,
        val message: String = "Analyzing your notes and creating flashcards...",
    ) : CardGenerationInputUiState()
    data class OcrPreview(
        val source: GenerationSource,
        val rawText: String,
        val characterCount: Int,
        val generationError: String? = null,
    ) : CardGenerationInputUiState()
    data class Success(
        val source: GenerationSource,
        val jobId: String,
    ) : CardGenerationInputUiState()
    data class Error(
        val source: GenerationSource?,
        val message: String,
    ) : CardGenerationInputUiState()
}

@HiltViewModel
class CardGenerationInputViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val extractTextForOcrUseCase: ExtractTextForOcrUseCase,
    private val generateCardsFromOcrTextUseCase: GenerateCardsFromOcrTextUseCase,
    private val generatedVocabularyCardStore: GeneratedVocabularyCardStore,
) : ViewModel() {

    private val source = runCatching {
        val type = Uri.decode(savedStateHandle.get<String>("sourceType").orEmpty())
        val uri = Uri.decode(savedStateHandle.get<String>("uri").orEmpty())
        val mimeType = Uri.decode(savedStateHandle.get<String>("mimeType").orEmpty())

        if (type.isBlank() || uri.isBlank() || mimeType.isBlank()) return@runCatching null

        GenerationSource(
            type = type,
            uri = Uri.parse(uri),
            mimeType = mimeType,
            name = Uri.decode(savedStateHandle.get<String>("name").orEmpty()).takeIf { it.isNotBlank() },
        )
    }.getOrNull()

    private val _uiState = MutableStateFlow<CardGenerationInputUiState>(
        source?.let { CardGenerationInputUiState.Loading(it, initialLoadingMessage(it)) }
            ?: CardGenerationInputUiState.Error(null, "Missing generation source")
    )
    val uiState: StateFlow<CardGenerationInputUiState> = _uiState.asStateFlow()

    init {
        processSource()
    }

    fun retry() {
        processSource()
    }

    fun updateRawText(value: String) {
        val state = _uiState.value as? CardGenerationInputUiState.OcrPreview ?: return
        _uiState.value = state.copy(
            rawText = value,
            characterCount = value.length,
            generationError = null,
        )
    }

    fun generateCardsFromPreview() {
        val state = _uiState.value as? CardGenerationInputUiState.OcrPreview ?: return
        val text = state.rawText.trim()
        if (!OcrTextProcessor.hasReadableText(text)) {
            _uiState.value = state.copy(generationError = OcrTextProcessor.NO_READABLE_TEXT_MESSAGE)
            return
        }
        generateFromText(state.source, text)
    }

    private fun processSource() {
        val generationSource = source
        if (generationSource == null) {
            _uiState.value = CardGenerationInputUiState.Error(null, "Missing generation source")
            return
        }

        extractText(generationSource)
    }

    private fun extractText(generationSource: GenerationSource) {
        viewModelScope.launch {
            _uiState.value = CardGenerationInputUiState.Loading(generationSource, extractionMessage(generationSource))
            extractTextForOcrUseCase(generationSource.uri, generationSource.mimeType)
                .onSuccess { ocrResult ->
                    _uiState.value = CardGenerationInputUiState.OcrPreview(
                        source = generationSource,
                        rawText = ocrResult.text,
                        characterCount = ocrResult.characterCount,
                    )
                }
                .onFailure { error ->
                    _uiState.value = CardGenerationInputUiState.Error(
                        source = generationSource,
                        message = error.message ?: "Failed to extract text",
                    )
                }
        }
    }

    private fun generateFromText(generationSource: GenerationSource, rawText: String) {
        viewModelScope.launch {
            _uiState.value = CardGenerationInputUiState.Loading(generationSource, "Creating flashcards from reviewed text...")
            generateCardsFromOcrTextUseCase(rawText, if (generationSource.mimeType == "application/pdf") "pdf" else "scan")
                .onSuccess { generatedCards ->
                    handleGeneratedCards(generationSource, generatedCards)
                }
                .onFailure { error ->
                    _uiState.value = CardGenerationInputUiState.OcrPreview(
                        source = generationSource,
                        rawText = rawText,
                        characterCount = rawText.length,
                        generationError = error.message ?: "Failed to generate cards",
                    )
                }
        }
    }

    private fun handleGeneratedCards(
        generationSource: GenerationSource,
        generatedCards: List<com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard>,
    ) {
        if (generatedCards.isEmpty()) {
            _uiState.value = CardGenerationInputUiState.Error(
                source = generationSource,
                message = "No cards were generated from this source.",
            )
        } else {
            _uiState.value = CardGenerationInputUiState.Success(
                generationSource,
                generatedVocabularyCardStore.save(generatedCards),
            )
        }
    }

    private fun initialLoadingMessage(source: GenerationSource): String =
        extractionMessage(source)

    private fun extractionMessage(source: GenerationSource): String =
        if (source.mimeType == "application/pdf") {
            "Extracting text from your PDF..."
        } else {
            "Scanning text from your image..."
        }
}
