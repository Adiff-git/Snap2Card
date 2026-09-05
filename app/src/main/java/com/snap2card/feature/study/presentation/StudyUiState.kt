package com.snap2card.feature.study.presentation

import com.snap2card.feature.deck.domain.model.Card

sealed class StudyUiState {
    data object Loading : StudyUiState()
    data class Studying(
        val cards: List<Card>,
        val currentIndex: Int,
        val masteredCount: Int,
        val masteryPercent: Int,
    ) : StudyUiState()
    data object Completed : StudyUiState()
    data class Error(val message: String) : StudyUiState()
}
