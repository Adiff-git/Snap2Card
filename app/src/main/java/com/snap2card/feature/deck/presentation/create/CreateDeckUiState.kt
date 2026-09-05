package com.snap2card.feature.deck.presentation.create

sealed class CreateDeckUiState {
    data object Idle : CreateDeckUiState()
    data object Loading : CreateDeckUiState()
    data class Success(val deckId: String, val destination: DeckCreationDestination) : CreateDeckUiState()
    data class Error(val message: String) : CreateDeckUiState()
}
