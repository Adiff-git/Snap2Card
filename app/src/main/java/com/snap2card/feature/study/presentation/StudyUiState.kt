package com.snap2card.feature.study.presentation

import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.study.domain.model.ReviewResult

sealed class StudyUiState {
    data object Loading : StudyUiState()
    data class Studying(
        val cards: List<Card>,
        val currentIndex: Int,
        val isRevealed: Boolean,
        val masteryPercent: Int,
    ) : StudyUiState()
    data object Completed : StudyUiState()
    data class Error(val message: String) : StudyUiState()
}
