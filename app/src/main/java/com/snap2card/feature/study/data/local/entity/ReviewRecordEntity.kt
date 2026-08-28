package com.snap2card.feature.study.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "review_records",
    indices = [Index("cardId"), Index("deckId")]
)
data class ReviewRecordEntity(
    @PrimaryKey val id: String,
    val cardId: String,
    val deckId: String,
    val result: String,       // "GOT_IT" | "AGAIN"
    val reviewedAt: Long,
)
