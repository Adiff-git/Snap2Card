package com.snap2card.feature.deck.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.usecase.AddCardUseCase
import com.snap2card.feature.deck.domain.usecase.DeleteCardUseCase
import com.snap2card.feature.deck.domain.usecase.GetCardsForDeckUseCase
import com.snap2card.feature.deck.domain.usecase.GetDeckByIdUseCase
import com.snap2card.feature.deck.domain.usecase.UpdateCardUseCase
import com.snap2card.feature.deck.presentation.editor.DeckEditorResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditDeckUiState {
    data object Loading : EditDeckUiState()
    data class Success(val deck: Deck, val cards: List<Card>) : EditDeckUiState()
    data class Error(val message: String) : EditDeckUiState()
}

@HiltViewModel
class EditDeckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
) : ViewModel() {

    private val deckId: String = checkNotNull(savedStateHandle["deckId"])

    private val _uiState = MutableStateFlow<EditDeckUiState>(EditDeckUiState.Loading)
    val uiState: StateFlow<EditDeckUiState> = _uiState.asStateFlow()

    init {
        loadDeck()
    }

    fun retry() {
        loadDeck()
    }

    private fun loadDeck() {
        viewModelScope.launch {
            _uiState.value = EditDeckUiState.Loading
            try {
                val deck = getDeckByIdUseCase(deckId)
                    ?: run {
                        _uiState.value = EditDeckUiState.Error("Deck not found")
                        return@launch
                    }
                getCardsForDeckUseCase(deckId).collect { cards ->
                    _uiState.value = EditDeckUiState.Success(deck, cards)
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.value = EditDeckUiState.Error(error.message ?: "Failed to load deck")
            }
        }
    }

    fun saveChanges(result: DeckEditorResult) {
        viewModelScope.launch {
            val original = (uiState.value as? EditDeckUiState.Success)?.cards ?: emptyList()
            val originalById = original.associateBy { it.id }
            result.cards.forEach { input ->
                val existing = input.id?.let(originalById::get)
                if (existing == null) {
                    addCardUseCase(deckId, input.front, input.back)      // new card → POST
                } else if (existing.front != input.front || existing.back != input.back) {
                    updateCardUseCase(existing.copy(front = input.front, back = input.back))
                }
            }
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            try {
                deleteCardUseCase(cardId)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.value = EditDeckUiState.Error(error.message ?: "Failed to delete card")
            }
        }
    }
}
