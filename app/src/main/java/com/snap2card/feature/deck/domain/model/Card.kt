package com.snap2card.feature.deck.domain.model

/** Domain entity for a single flashcard. */
data class Card(
    val id: String,
    val deckId: String,
    val front: String,
    val back: String,
    val createdAt: Long,
)
