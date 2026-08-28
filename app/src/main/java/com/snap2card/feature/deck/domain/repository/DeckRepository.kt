package com.snap2card.feature.deck.domain.repository

import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import kotlinx.coroutines.flow.Flow

/**
 * Deck repository interface.
 * Implementations decide whether to read from Room, API, or both.
 * UI and ViewModels only depend on this interface.
 */
interface DeckRepository {
    fun getDecks(): Flow<List<Deck>>
    suspend fun getDeckById(deckId: String): Deck?
    suspend fun createDeck(title: String, description: String): Deck
    suspend fun updateDeck(deck: Deck)
    suspend fun deleteDeck(deckId: String)

    fun getCardsForDeck(deckId: String): Flow<List<Card>>
    suspend fun addCard(deckId: String, front: String, back: String): Card
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(cardId: String)
    suspend fun addCards(cards: List<Card>)
}
