package com.snap2card.feature.deck.data.repository

import com.snap2card.core.util.DateUtil
import com.snap2card.feature.deck.data.local.dao.CardDao
import com.snap2card.feature.deck.data.local.dao.DeckDao
import com.snap2card.feature.deck.data.local.entity.CardEntity
import com.snap2card.feature.deck.data.local.entity.DeckEntity
import com.snap2card.feature.deck.data.mapper.toDeck
import com.snap2card.feature.deck.data.mapper.toDomain
import com.snap2card.feature.deck.data.mapper.toEntity
import com.snap2card.feature.deck.data.remote.DeckApiService
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.repository.DeckRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckRepositoryImpl @Inject constructor(
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    private val deckApiService: DeckApiService,
) : DeckRepository {

    override fun getDecks(): Flow<List<Deck>> = flow {
        try {
            val decks = deckApiService.getCategoryList().data.categories.map { it.toDeck() }
            emit(decks)
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            emitAll(localDecks())
        }
    }

    private fun localDecks(): Flow<List<Deck>> =
        deckDao.getAllDecks().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getDeckById(deckId: String): Deck? =
        deckDao.getDeckById(deckId)?.toDomain()

    override suspend fun createDeck(title: String, description: String): Deck {
        val now = DateUtil.now()
        val entity = DeckEntity(
            id = UUID.randomUUID().toString(),
            userId = "", // TODO: inject current user id
            title = title,
            description = description,
            createdAt = now,
            updatedAt = now,
        )
        deckDao.insertDeck(entity)
        return entity.toDomain()
    }

    override suspend fun updateDeck(deck: Deck) {
        deckDao.updateDeck(deck.toEntity(""))
    }

    override suspend fun deleteDeck(deckId: String) = deckDao.deleteDeck(deckId)

    override fun getCardsForDeck(deckId: String): Flow<List<Card>> =
        cardDao.getCardsForDeck(deckId).map { it.map { entity -> entity.toDomain() } }

    override suspend fun addCard(deckId: String, front: String, back: String): Card {
        val entity = CardEntity(
            id = UUID.randomUUID().toString(),
            deckId = deckId,
            front = front,
            back = back,
            createdAt = DateUtil.now(),
        )
        cardDao.insertCard(entity)
        return entity.toDomain()
    }

    override suspend fun updateCard(card: Card) = cardDao.updateCard(card.toEntity())
    override suspend fun deleteCard(cardId: String) = cardDao.deleteCard(cardId)
    override suspend fun addCards(cards: List<Card>) = cardDao.insertCards(cards.map { it.toEntity() })
}
