package com.snap2card.feature.snap2card.presentation.capture

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.core.util.FileUtil
import com.snap2card.feature.snap2card.domain.usecase.UploadImageForOcrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Snap2CardViewModel @Inject constructor(
    private val uploadImageForOcrUseCase: UploadImageForOcrUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<Snap2CardUiState>(Snap2CardUiState.Idle)
    val uiState: StateFlow<Snap2CardUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri, mimeType: String) {
        viewModelScope.launch {
            _uiState.value = Snap2CardUiState.Uploading()
            // Transition to Processing after upload completes
            _uiState.value = Snap2CardUiState.Processing
            uploadImageForOcrUseCase(uri, mimeType)
                .onSuccess { cards -> _uiState.value = Snap2CardUiState.Success(cards) }
                .onFailure { e -> _uiState.value = Snap2CardUiState.Error(e.message ?: "Upload failed") }
        }
    }

    fun reset() { _uiState.value = Snap2CardUiState.Idle }
}
