package com.snap2card.feature.deck.presentation.list

import com.snap2card.feature.deck.domain.model.Deck

sealed class DeckListUiState {
    data object Loading : DeckListUiState()
    data class Success(
        val decks: List<Deck>,
        val deletingDeckIds: Set<String> = emptySet(),
        val deleteMessage: String? = null,
        val deleteError: String? = null,
    ) : DeckListUiState()
    data object Empty : DeckListUiState()
    data class Error(val message: String) : DeckListUiState()
}
