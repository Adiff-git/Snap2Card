package com.snap2card.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.home.domain.usecase.GetRecentDecksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getRecentDecksUseCase: GetRecentDecksUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getRecentDecksUseCase()
        .map { decks ->
            HomeUiState.Success(
                greeting = "Good day!",
                recentDecks = decks,
                dailyGoal = 20,
                reviewedToday = 0,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)
}
