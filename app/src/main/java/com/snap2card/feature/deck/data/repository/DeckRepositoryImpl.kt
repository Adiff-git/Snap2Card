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
import com.snap2card.feature.deck.data.remote.dto.CardCategorizeRequest
import com.snap2card.feature.deck.data.remote.dto.CardCreateRequest
import com.snap2card.feature.deck.data.remote.dto.CategoryCreateRequest
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.repository.DeckRepository
import com.snap2card.feature.snap2card.data.remote.dto.CardEditRequest
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
            deckDao.insertDecks(decks.map { it.toEntity(userId = "") })
            emit(decks)
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            emitAll(localDecks())
        }
    }

    private fun localDecks(): Flow<List<Deck>> =
        deckDao.getAllDecks().map { entities -> entities.map { it.toDomain() } }

    private suspend fun fetchCategory(deckId: String) = deckApiService.getCategory(deckId).data

    override suspend fun getDeckById(deckId: String): Deck? = try {
        fetchCategory(deckId).toDeck(deckId).also { deckDao.insertDeck(it.toEntity(userId = "")) }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        deckDao.getDeckById(deckId)?.toDomain()
    }

    override suspend fun createDeck(title: String, description: String): Deck {
        val now = DateUtil.now()
        val categoryName = title.uppercase().take(20).ifBlank { "GENERAL" }
        val deckId = try {
            deckApiService.createCategory(CategoryCreateRequest(name = categoryName)).data.categoryId
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            UUID.randomUUID().toString()
        }
        val entity = DeckEntity(
            id = deckId,
            userId = "",
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

    override fun getCardsForDeck(deckId: String): Flow<List<Card>> = flow {
        try {
            val cardIds = fetchCategory(deckId).cardIds
            val cards = if (cardIds.isEmpty()) emptyList()
            else deckApiService.getCards(cardIds.joinToString(",")).data.map { it.toDomain(deckId) }
            cardDao.insertCards(cards.map { it.toEntity() })
            emit(cards)
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            emitAll(localCards(deckId))
        }
    }


    private fun localCards(deckId: String): Flow<List<Card>> =
        cardDao.getCardsForDeck(deckId).map { it.map { entity -> entity.toDomain() } }

    override suspend fun addCard(deckId: String, front: String, back: String): Card {
        val card = try {
            val response = deckApiService.createCard(
                CardCreateRequest(
                    name = front.take(60).ifBlank { "Manual Card" },
                    frontSide = front,
                    backSide = back,
                )
            )
            val cardId = response.data?.id ?: response.data?.cards?.firstOrNull()?.id
            ?: error("Card create response missing id")
            deckApiService.categorizeCard(CardCategorizeRequest(cardId = cardId, categoryIds = listOf(deckId)))
            Card(id = cardId, deckId = deckId, front = front, back = back, createdAt = DateUtil.now())
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            localCard(deckId, front, back)
        }
        cardDao.insertCard(card.toEntity())
        return card
    }

    private fun localCard(deckId: String, front: String, back: String): Card = CardEntity(
        id = UUID.randomUUID().toString(),
        deckId = deckId,
        front = front,
        back = back,
        createdAt = DateUtil.now(),
    ).toDomain()

    override suspend fun updateCard(card: Card) {
        try {
            deckApiService.updateCard(
                CardEditRequest(
                    id = card.id,
                    frontSide = card.front,
                    backSide = card.back
                )
            )
        } catch (error: Exception) {
            if (error is CancellationException) throw error
        }
        cardDao.updateCard(card.toEntity())
    }
    override suspend fun deleteCard(cardId: String) = cardDao.deleteCard(cardId)
    override suspend fun addCards(cards: List<Card>) = cardDao.insertCards(cards.map { it.toEntity() })
}
