package com.snap2card.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.domain.usecase.DeleteDeckUseCase
import com.snap2card.feature.home.domain.model.RecentDeck
import com.snap2card.feature.home.domain.usecase.GetDashboardUseCase
import com.snap2card.feature.settings.domain.usecase.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeDailyGoal()
        loadDashboard()
    }

    private fun observeDailyGoal() {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    val dailyGoalTotal = settings.dailyGoalCards.coerceAtLeast(1)
                    _uiState.value = currentState.copy(
                        dailyGoalTotal = dailyGoalTotal,
                        dailyGoalCompleted = currentState.dailyGoalCompleted.coerceIn(0, dailyGoalTotal),
                    )
                }
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getDashboardUseCase()
                .onSuccess { data ->
                    _uiState.value = HomeUiState.Success(
                        userName = data.userName,
                        userPhotoUrl = data.userPhotoUrl,
                        streakCount = data.streakCount,
                        recentDecks = data.recentDecks,
                        dailyGoalTotal = data.dailyGoalTotal,
                        dailyGoalCompleted = data.dailyGoalCompleted,
                    )
                }
                .onFailure { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Failed to load dashboard")
                }
        }
    }

    fun deleteDeck(deck: RecentDeck) {
        val currentState = _uiState.value as? HomeUiState.Success ?: return
        if (deck.id in currentState.deletingDeckIds) return

        _uiState.value = currentState.copy(
            deletingDeckIds = currentState.deletingDeckIds + deck.id,
            deleteMessage = null,
            deleteError = null,
        )

        viewModelScope.launch {
            try {
                deleteDeckUseCase(deck.id)
                _uiState.update { state ->
                    if (state is HomeUiState.Success) {
                        state.copy(
                            recentDecks = state.recentDecks.filterNot { it.id == deck.id },
                            deletingDeckIds = state.deletingDeckIds - deck.id,
                            deleteMessage = "Deleted \"${deck.title}\".",
                            deleteError = null,
                        )
                    } else state
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.update { state ->
                    if (state is HomeUiState.Success) {
                        state.copy(
                            deletingDeckIds = state.deletingDeckIds - deck.id,
                            deleteMessage = null,
                            deleteError = error.message ?: "Could not delete deck. Try again.",
                        )
                    } else state
                }
            }
        }
    }

    fun clearDeleteNotice() {
        _uiState.update { state ->
            if (state is HomeUiState.Success) state.copy(deleteMessage = null, deleteError = null) else state
        }
    }
}
