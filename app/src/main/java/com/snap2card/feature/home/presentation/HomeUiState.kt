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
        val deletingDeckIds: Set<String> = emptySet(),
        val deleteMessage: String? = null,
        val deleteError: String? = null,
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
