package com.snap2card.feature.home.domain.usecase

import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Returns the N most recently updated decks for the Home dashboard. */
class GetRecentDecksUseCase @Inject constructor(
    private val deckRepository: DeckRepository
) {
    operator fun invoke(limit: Int = 5): Flow<List<Deck>> =
        deckRepository.getDecks().map { it.take(limit) }
}
