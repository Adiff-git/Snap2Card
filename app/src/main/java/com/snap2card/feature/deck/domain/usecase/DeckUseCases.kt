package com.snap2card.feature.deck.domain.usecase

import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDecksUseCase @Inject constructor(private val repo: DeckRepository) {
    operator fun invoke(): Flow<List<Deck>> = repo.getDecks()
}

class GetDeckByIdUseCase @Inject constructor(private val repo: DeckRepository) {
    suspend operator fun invoke(deckId: String): Deck? = repo.getDeckById(deckId)
}

class CreateDeckUseCase @Inject constructor(private val repo: DeckRepository) {
    suspend operator fun invoke(title: String, description: String): Deck =
        repo.createDeck(title, description)
}

class DeleteDeckUseCase @Inject constructor(private val repo: DeckRepository) {
    suspend operator fun invoke(deckId: String) = repo.deleteDeck(deckId)
}

class GetCardsForDeckUseCase @Inject constructor(private val repo: DeckRepository) {
    operator fun invoke(deckId: String): Flow<List<Card>> = repo.getCardsForDeck(deckId)
}

class AddCardUseCase @Inject constructor(private val repo: DeckRepository) {
    suspend operator fun invoke(deckId: String, front: String, back: String) =
        repo.addCard(deckId, front, back)
}

class UpdateCardUseCase @Inject constructor(private val repo: DeckRepository) {
    suspend operator fun invoke(card: Card) = repo.updateCard(card)
}
