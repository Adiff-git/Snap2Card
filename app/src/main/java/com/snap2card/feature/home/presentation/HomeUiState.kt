package com.snap2card.feature.home.presentation

import com.snap2card.feature.deck.domain.model.Deck

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val greeting: String,
        val recentDecks: List<Deck>,
        val dailyGoal: Int,
        val reviewedToday: Int,
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
