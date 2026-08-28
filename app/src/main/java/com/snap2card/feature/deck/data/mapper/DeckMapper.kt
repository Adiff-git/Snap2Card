package com.snap2card.feature.deck.data.mapper

import com.snap2card.feature.deck.data.local.entity.CardEntity
import com.snap2card.feature.deck.data.local.entity.DeckEntity
import com.snap2card.feature.deck.data.remote.dto.CardDto
import com.snap2card.feature.deck.data.remote.dto.DeckDto
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.core.util.DateUtil

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

fun CardEntity.toDomain() = Card(
    id = id, deckId = deckId, front = front, back = back, createdAt = createdAt,
)

fun Card.toEntity() = CardEntity(
    id = id, deckId = deckId, front = front, back = back, createdAt = createdAt,
)

fun CardDto.toDomain() = Card(
    id = id, deckId = deckId, front = front, back = back, createdAt = createdAt,
)
