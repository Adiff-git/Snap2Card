package com.snap2card.feature.snap2card.presentation.capture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.domain.repository.DeckRepository
import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManualCardEditorUiState(
    val cards: List<DeckEditorCardInput> = listOf(DeckEditorCardInput()),
    val showValidation: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedCardCount: Int? = null,
) {
    val isValid: Boolean = cards.all { it.front.isNotBlank() && it.back.isNotBlank() }
    val saveText: String = if (isSaving) "Saving..." else "Save ${cards.size} ${if (cards.size == 1) "Card" else "Cards"}"
}

@HiltViewModel
class ManualCardEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository,
) : ViewModel() {

    private val deckId: String = checkNotNull(savedStateHandle["deckId"])

    private val _uiState = MutableStateFlow(ManualCardEditorUiState())
    val uiState: StateFlow<ManualCardEditorUiState> = _uiState.asStateFlow()

    fun updateFront(index: Int, value: String) = updateCard(index) { it.copy(front = value) }
    fun updateBack(index: Int, value: String) = updateCard(index) { it.copy(back = value) }
    fun addCard() {
        _uiState.update { it.copy(cards = it.cards + DeckEditorCardInput()) }
    }

    fun deleteCard(index: Int) {
        _uiState.update { state ->
            if (state.cards.size == 1) state
            else state.copy(cards = state.cards.filterIndexed { i, _ -> i != index })
        }
    }

    fun markValidationShown() {
        _uiState.update { it.copy(showValidation = true) }
    }

    fun save() {
        val state = _uiState.value
        if (!state.isValid) {
            markValidationShown()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching {
                state.cards.forEach { card -> deckRepository.addCard(deckId, card.front, card.back) }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, savedCardCount = state.cards.size) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Some cards failed to save") }
            }
        }
    }

    private fun updateCard(index: Int, transform: (DeckEditorCardInput) -> DeckEditorCardInput) {
        _uiState.update { state ->
            if (index !in state.cards.indices) state
            else state.copy(cards = state.cards.mapIndexed { i, c -> if (i == index) transform(c) else c })
        }
    }
}
