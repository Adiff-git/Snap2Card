package com.snap2card.feature.study.domain.usecase

import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.model.ReviewResult
import javax.inject.Inject
import kotlin.math.max

data class SrsState(
    val easeFactor: Double = 2.5,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val lastReviewedAt: Long? = null,
)

class CalculateNextReviewUseCase @Inject constructor() {

    fun deriveState(reviews: List<ReviewRecord>): SrsState =
        reviews.sortedBy { it.reviewedAt }.fold(SrsState()) { state, review ->
            applyReview(state, review.result).copy(lastReviewedAt = review.reviewedAt)
        }

    fun applyReview(state: SrsState, result: ReviewResult): SrsState = when (result) {
        ReviewResult.AGAIN -> state.copy(
            repetitions = 0,
            interval = 0,
            easeFactor = max(1.3, state.easeFactor - 0.2),
        )
        ReviewResult.GOT_IT -> {
            val newReps = state.repetitions + 1
            val newInterval = when (newReps) {
                1 -> 1
                2 -> 6
                else -> (state.interval * state.easeFactor).toInt().coerceAtLeast(1)
            }
            state.copy(repetitions = newReps, interval = newInterval, easeFactor = state.easeFactor + 0.1)
        }
    }

    fun isDue(state: SrsState, now: Long): Boolean {
        val last = state.lastReviewedAt ?: return true
        return last + state.interval * 86_400_000L <= now
    }
}