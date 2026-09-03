package com.snap2card.feature.snap2card.presentation.capture

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.card_generation.domain.model.VocabularyGenerationDefaults
import com.snap2card.feature.card_generation.domain.repository.GeneratedVocabularyCardStore
import com.snap2card.feature.card_generation.domain.usecase.GenerateVocabularyFromTextUseCase
import com.snap2card.feature.snap2card.domain.service.OcrTextProcessor
import com.snap2card.feature.snap2card.domain.service.TextRecognitionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Snap2CardViewModel @Inject constructor(
    private val textRecognitionService: TextRecognitionService,
    private val generateVocabularyFromTextUseCase: GenerateVocabularyFromTextUseCase,
    private val generatedVocabularyCardStore: GeneratedVocabularyCardStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Snap2CardUiState>(Snap2CardUiState.Idle)
    val uiState: StateFlow<Snap2CardUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri) {
        if (_uiState.value is Snap2CardUiState.ExtractingText || _uiState.value is Snap2CardUiState.GeneratingCards) {
            return
        }

        viewModelScope.launch {
            _uiState.value = Snap2CardUiState.ExtractingText

            val ocrResult = textRecognitionService.recognizeText(uri).getOrElse {
                _uiState.value = Snap2CardUiState.Error("Could not read text from this image. Try another photo.")
                return@launch
            }

            if (!OcrTextProcessor.hasReadableText(ocrResult.text)) {
                _uiState.value = Snap2CardUiState.Error(OcrTextProcessor.NO_READABLE_TEXT_MESSAGE)
                return@launch
            }

            _uiState.value = Snap2CardUiState.GeneratingCards(ocrResult.characterCount)
            generateVocabularyFromTextUseCase(
                text = ocrResult.text,
                level = VocabularyGenerationDefaults.LEVEL,
                count = VocabularyGenerationDefaults.COUNT,
                includePhrases = VocabularyGenerationDefaults.INCLUDE_PHRASES,
                sourceType = "scan",
            ).onSuccess { cards ->
                if (cards.isEmpty()) {
                    _uiState.value = Snap2CardUiState.Error("No cards were generated from this source.")
                } else {
                    _uiState.value = Snap2CardUiState.Success(generatedVocabularyCardStore.save(cards))
                }
            }.onFailure { error ->
                _uiState.value = Snap2CardUiState.Error(
                    error.message?.takeIf { it.isNotBlank() }
                        ?: "Could not generate cards. Check your connection and try again."
                )
            }
        }
    }

    fun onInputCancelled() {
        _uiState.value = Snap2CardUiState.Idle
    }

    fun onInputUnavailable() {
        _uiState.value = Snap2CardUiState.Error("Could not open this image. Try another photo.")
    }

    fun onCameraPermissionDenied() {
        _uiState.value = Snap2CardUiState.Error("Camera permission is required to scan from camera.")
    }

    fun reset() { _uiState.value = Snap2CardUiState.Idle }
}
