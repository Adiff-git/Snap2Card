package com.snap2card.feature.card_generation.presentation

import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput

sealed class GeneratedCardsUiState {
    data object Loading : GeneratedCardsUiState()
    data class Success(
        val jobId: String,
        val category: String,
        val cards: List<DeckEditorCardInput>,
    ) : GeneratedCardsUiState()
    data class Error(val message: String) : GeneratedCardsUiState()
}
