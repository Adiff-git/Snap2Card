package com.snap2card.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.history.domain.model.HistorySession
import com.snap2card.feature.history.domain.usecase.GetStudyActivityUseCase
import com.snap2card.feature.history.domain.usecase.GetStudyHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getStudyActivityUseCase: GetStudyActivityUseCase,
    private val getStudyHistoryUseCase: GetStudyHistoryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading

            val activity = getStudyActivityUseCase()
                .onFailure { e -> _uiState.value = HistoryUiState.Error(e.message ?: "Failed to load activity") }
                .getOrNull() ?: return@launch

            getStudyHistoryUseCase(page = 1, limit = 20)
                .onSuccess { sessions ->
                    _uiState.value = if (sessions.isEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Loaded(
                            streakDays = activity.currentStreakDays,
                            cardsThisMonth = activity.cardsThisMonth,
                            dailyCounts = activity.dailyCounts,
                            sessionsByDay = groupByDay(sessions),
                        )
                    }
                }
                .onFailure { e -> _uiState.value = HistoryUiState.Error(e.message ?: "Failed to load history") }
        }
    }

    private fun groupByDay(sessions: List<HistorySession>): List<HistoryDaySection> {
        val today = startOfDay(System.currentTimeMillis())
        val yesterday = today - DAY_MILLIS
        val fmt = SimpleDateFormat("MMM d", Locale.getDefault())

        return sessions
            .sortedByDescending { it.completedAt }
            .groupBy { startOfDay(it.completedAt) }
            .toSortedMap(compareByDescending { it })
            .map { (day, daySessions) ->
                val label = when (day) {
                    today -> "Today, ${fmt.format(day)}"
                    yesterday -> "Yesterday, ${fmt.format(day)}"
                    else -> fmt.format(day)
                }
                HistoryDaySection(label, daySessions)
            }
    }

    private fun startOfDay(millis: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private companion object { const val DAY_MILLIS = 24 * 60 * 60 * 1000L }
}