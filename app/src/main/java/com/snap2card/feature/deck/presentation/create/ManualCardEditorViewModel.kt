package com.snap2card.feature.deck.presentation.create

import androidx.lifecycle.ViewModel
import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ManualCardEditorUiState(
    val cards: List<DeckEditorCardInput> = listOf(DeckEditorCardInput()),
    val showValidation: Boolean = false,
) {
    val isValid: Boolean = cards.all { it.front.isNotBlank() && it.back.isNotBlank() }
    val saveText: String = "Save ${cards.size} ${if (cards.size == 1) "Card" else "Cards"}"
}

@HiltViewModel
class ManualCardEditorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ManualCardEditorUiState())
    val uiState: StateFlow<ManualCardEditorUiState> = _uiState.asStateFlow()

    fun updateFront(index: Int, value: String) {
        updateCard(index) { it.copy(front = value) }
    }

    fun updateBack(index: Int, value: String) {
        updateCard(index) { it.copy(back = value) }
    }

    fun addCard() {
        _uiState.update { state ->
            state.copy(cards = state.cards + DeckEditorCardInput())
        }
    }

    fun deleteCard(index: Int) {
        _uiState.update { state ->
            if (state.cards.size == 1) {
                state
            } else {
                state.copy(cards = state.cards.filterIndexed { i, _ -> i != index })
            }
        }
    }

    fun markValidationShown() {
        _uiState.update { it.copy(showValidation = true) }
    }

    fun save(): Boolean {
        val state = _uiState.value
        if (!state.isValid) {
            markValidationShown()
            return false
        }

        // TODO: Persist these cards once the flow provides a real deck/category id.
        // Existing repository APIs require deckId before calling addCard/addCards.
        return true
    }

    private fun updateCard(index: Int, transform: (DeckEditorCardInput) -> DeckEditorCardInput) {
        _uiState.update { state ->
            if (index !in state.cards.indices) {
                state
            } else {
                state.copy(
                    cards = state.cards.mapIndexed { i, card ->
                        if (i == index) transform(card) else card
                    },
                )
            }
        }
    }
}
