package com.snap2card.feature.study.domain.model

/** Records the result of reviewing a single card during a study session. */
data class ReviewRecord(
    val id: String,
    val cardId: String,
    val deckId: String,
    val result: ReviewResult,
    val reviewedAt: Long,
)

enum class ReviewResult {
    GOT_IT,
    AGAIN,
}
