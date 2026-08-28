package com.snap2card.feature.snap2card.presentation.capture

import com.snap2card.feature.snap2card.domain.model.GeneratedCard

sealed class Snap2CardUiState {
    data object Idle : Snap2CardUiState()
    data class Uploading(val progress: Float = 0f) : Snap2CardUiState()
    data object Processing : Snap2CardUiState()
    data class Success(val cards: List<GeneratedCard>) : Snap2CardUiState()
    data class Error(val message: String) : Snap2CardUiState()
}
