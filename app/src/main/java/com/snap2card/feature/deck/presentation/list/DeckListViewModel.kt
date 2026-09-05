package com.snap2card.feature.deck.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.usecase.DeleteDeckUseCase
import com.snap2card.feature.deck.domain.usecase.GetDecksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckListViewModel @Inject constructor(
    getDecksUseCase: GetDecksUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
) : ViewModel() {

    private val deletedDeckIds = MutableStateFlow<Set<String>>(emptySet())
    private val deletingDeckIds = MutableStateFlow<Set<String>>(emptySet())
    private val deleteError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DeckListUiState> = combine(
        getDecksUseCase(),
        deletedDeckIds,
        deletingDeckIds,
        deleteError,
    ) { decks: List<Deck>, deletedIds: Set<String>, deletingIds: Set<String>, error: String? ->
        val visibleDecks = decks.filterNot { it.id in deletedIds }
        if (visibleDecks.isEmpty()) DeckListUiState.Empty
        else DeckListUiState.Success(
            decks = visibleDecks,
            deletingDeckIds = deletingIds,
            deleteError = error,
        )
    }
        .catch { error ->
            emit(DeckListUiState.Error(error.message ?: "Failed to load decks"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeckListUiState.Loading,
        )

    fun deleteDeck(deckId: String) {
        if (deckId in deletingDeckIds.value) return

        deletingDeckIds.update { it + deckId }
        deleteError.value = null

        viewModelScope.launch {
            try {
                deleteDeckUseCase(deckId)
                deletedDeckIds.update { it + deckId }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                deleteError.value = error.message ?: "Could not delete deck. Try again."
            } finally {
                deletingDeckIds.update { it - deckId }
            }
        }
    }

    fun clearDeleteError() {
        deleteError.value = null
    }
}
