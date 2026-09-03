package com.snap2card.feature.deck.data.mapper

import com.snap2card.core.util.DateUtil
import com.snap2card.feature.deck.data.local.entity.CardEntity
import com.snap2card.feature.deck.data.local.entity.DeckEntity
import com.snap2card.feature.deck.data.remote.dto.ApiTimeDto
import com.snap2card.feature.deck.data.remote.dto.CardDto
import com.snap2card.feature.deck.data.remote.dto.CategoryDto
import com.snap2card.feature.deck.data.remote.dto.DeckDto
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import java.time.OffsetDateTime
import java.time.ZoneOffset

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
        cardCount = 0,
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

fun CardDto.toDomain() = Card(
    id = id, deckId = deckId, front = front, back = back, createdAt = createdAt,
)

private fun ApiTimeDto.toEpochMillis(): Long {
    val offset = runCatching { ZoneOffset.of(gmt) }.getOrDefault(ZoneOffset.UTC)
    return OffsetDateTime.of(year, month, day, hour, minute, second, 0, offset)
        .toInstant()
        .toEpochMilli()
}
