package com.snap2card.feature.snap2card.presentation.capture

sealed class Snap2CardUiState {
    data object Idle : Snap2CardUiState()
    data object ExtractingText : Snap2CardUiState()
    data class GeneratingCards(val characterCount: Int) : Snap2CardUiState()
    data class Success(val jobId: String) : Snap2CardUiState()
    data class Error(val message: String) : Snap2CardUiState()
}
