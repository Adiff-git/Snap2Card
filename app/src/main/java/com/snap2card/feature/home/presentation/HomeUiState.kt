package com.snap2card.feature.home.presentation

import com.snap2card.feature.home.domain.model.RecentDeck

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val userName: String,
        val userPhotoUrl: String?,
        val streakCount: Int,
        val recentDecks: List<RecentDeck>,
        val dailyGoalTotal: Int,
        val dailyGoalCompleted: Int,
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
