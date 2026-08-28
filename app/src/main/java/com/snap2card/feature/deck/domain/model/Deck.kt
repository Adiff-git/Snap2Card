package com.snap2card.feature.deck.domain.model

/** Domain entity for a flashcard deck. Maps from DeckEntity (Room) or DeckDto (API). */
data class Deck(
    val id: String,
    val title: String,
    val description: String,
    val cardCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
)
