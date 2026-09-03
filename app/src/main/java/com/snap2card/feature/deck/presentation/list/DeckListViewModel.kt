package com.snap2card.feature.deck.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.usecase.GetDecksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DeckListViewModel @Inject constructor(
    getDecksUseCase: GetDecksUseCase,
) : ViewModel() {

    val uiState: StateFlow<DeckListUiState> = getDecksUseCase()
        .map { decks: List<Deck> ->
            if (decks.isEmpty()) DeckListUiState.Empty
            else DeckListUiState.Success(decks)
        }
        .catch { error ->
            emit(DeckListUiState.Error(error.message ?: "Failed to load decks"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeckListUiState.Loading,
        )
}
