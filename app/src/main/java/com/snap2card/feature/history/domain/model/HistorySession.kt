package com.snap2card.feature.history.domain.model

data class HistorySession(
    val id: String,
    val title: String,       // e.g. "Biology 101"
    val cardsReviewed: Int,
    val completedAt: Long,   // epoch millis
    val status: SessionStatus,
)

enum class SessionStatus { COMPLETED, INCOMPLETE }