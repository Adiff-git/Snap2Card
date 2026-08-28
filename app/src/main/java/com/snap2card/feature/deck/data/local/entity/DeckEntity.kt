package com.snap2card.feature.deck.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
)
