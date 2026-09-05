package com.snap2card.feature.deck.data.mapper

import com.snap2card.core.util.DateUtil
import com.snap2card.core.util.DateUtil.toEpochMillis
import com.snap2card.feature.deck.data.local.entity.CardEntity
import com.snap2card.feature.deck.data.local.entity.DeckEntity
import com.snap2card.feature.deck.data.remote.dto.CardDetailDto
import com.snap2card.feature.deck.data.remote.dto.CardDto
import com.snap2card.feature.deck.data.remote.dto.CategoryDto
import com.snap2card.feature.deck.data.remote.dto.CategoryRetrieveData
import com.snap2card.feature.deck.data.remote.dto.DeckDto
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck

fun DeckEntity.toDomain(cardCount: Int = 0) = Deck(
    id = id, title = title, description = description,
    cardCount = cardCount, createdAt = createdAt, updatedAt = updatedAt,
)

fun Deck.toEntity(userId: String) = DeckEntity(
    id = id, userId = userId, title = title, description = description,
    createdAt = createdAt, updatedAt = updatedAt,
)

fun DeckDto.toDomain() = Deck(
    id = id, title = title, description = description,
    cardCount = cardCount, createdAt = createdAt, updatedAt = updatedAt,
)

fun DeckDto.toEntity(userId: String) = DeckEntity(
    id = id, userId = userId, title = title, description = description,
    createdAt = createdAt, updatedAt = updatedAt,
)

fun CategoryDto.toDeck(): Deck {
    val createdAtMillis = createdAt?.toEpochMillis() ?: DateUtil.now()

    return Deck(
        id = id,
        title = name,
        description = "",
        cardCount = numOfCard ?: 0,
        createdAt = createdAtMillis,
        updatedAt = createdAtMillis,
    )
}

fun CategoryRetrieveData.toDeck(id: String): Deck {
    val createdAtMillis = createdAt?.toEpochMillis() ?: DateUtil.now()

    return Deck(
        id = id,
        title = name,
        description = "",
        cardCount = numOfCard ?: 0,
        createdAt = createdAtMillis,
        updatedAt = createdAtMillis,
    )
}

fun CardEntity.toDomain() = Card(
    id = id, deckId = deckId, front = front, back = back, createdAt = createdAt,
)

fun Card.toEntity() = CardEntity(
    id = id, deckId = deckId, front = front, back = back, createdAt = createdAt,
)

fun CardDto.toDomain(deckId: String) = Card(
    id = id,
    deckId = deckId,
    front = frontSide,
    back = backSide,
    createdAt = DateUtil.now(),
)

fun CardDetailDto.toDomain(deckId: String) = Card(
    id = id,
    deckId = deckId,
    front = frontSide,
    back = backSide,
    createdAt = DateUtil.now(),
)
