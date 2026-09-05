package com.snap2card.feature.history.presentation

import com.snap2card.feature.history.domain.model.DayCount
import com.snap2card.feature.history.domain.model.HistorySession

sealed class HistoryUiState {
    data object Loading : HistoryUiState()
    data class Loaded(
        val streakDays: Int,
        val cardsThisMonth: Int,
        val dailyCounts: List<DayCount>,
        val sessionsByDay: List<HistoryDaySection>, // grouped for the "Today, Oct 24" headers
    ) : HistoryUiState()
    data object Empty : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

data class HistoryDaySection(
    val label: String, // "Today, Oct 24" / "Yesterday, Oct 23" / "Oct 22"
    val sessions: List<HistorySession>,
)