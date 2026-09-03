package com.snap2card.feature.snap2card.presentation.review

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput
import com.snap2card.feature.snap2card.domain.usecase.UploadImageForOcrUseCase
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
    data class Loading(val source: GenerationSource) : CardGenerationInputUiState()
    data class Success(
        val source: GenerationSource,
        val cards: List<DeckEditorCardInput>,
    ) : CardGenerationInputUiState()
    data class Error(
        val source: GenerationSource?,
        val message: String,
    ) : CardGenerationInputUiState()
}

@HiltViewModel
class CardGenerationInputViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val uploadImageForOcrUseCase: UploadImageForOcrUseCase,
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
        source?.let { CardGenerationInputUiState.Loading(it) }
            ?: CardGenerationInputUiState.Error(null, "Missing generation source")
    )
    val uiState: StateFlow<CardGenerationInputUiState> = _uiState.asStateFlow()

    init {
        generate()
    }

    fun retry() {
        generate()
    }

    fun saveReviewedCards(cards: List<DeckEditorCardInput>): Boolean {
        if (cards.any { it.front.isBlank() || it.back.isBlank() }) return false

        // TODO: Persist reviewed generated cards once this flow has a real deck/category id.
        // Existing save APIs need a target deck/category before calling DeckRepository.addCard/addCards.
        return true
    }

    private fun generate() {
        val generationSource = source
        if (generationSource == null) {
            _uiState.value = CardGenerationInputUiState.Error(null, "Missing generation source")
            return
        }

        viewModelScope.launch {
            _uiState.value = CardGenerationInputUiState.Loading(generationSource)
            uploadImageForOcrUseCase(generationSource.uri, generationSource.mimeType, generationSource.displayName)
                .onSuccess { generated ->
                    val cards = listOf(
                        DeckEditorCardInput(front = generated.front, back = generated.back)
                    )
                    _uiState.value = CardGenerationInputUiState.Success(generationSource, cards)
                }
                .onFailure { error ->
                    _uiState.value = CardGenerationInputUiState.Error(
                        source = generationSource,
                        message = error.message ?: "Failed to generate cards",
                    )
                }
        }
    }
}