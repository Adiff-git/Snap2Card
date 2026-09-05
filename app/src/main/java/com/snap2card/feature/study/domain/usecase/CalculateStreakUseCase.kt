package com.snap2card.feature.study.domain.usecase

import com.snap2card.feature.study.domain.model.ReviewRecord
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CalculateStreakUseCase @Inject constructor() {
    operator fun invoke(reviews: List<ReviewRecord>): Int {
        val zone = ZoneId.systemDefault()
        val reviewedDates = reviews
            .map { Instant.ofEpochMilli(it.reviewedAt).atZone(zone).toLocalDate() }
            .toSortedSet()

        if (reviewedDates.isEmpty()) return 0
        val today = LocalDate.now(zone)
        var cursor = when (reviewedDates.last()) {
            today -> today
            today.minusDays(1) -> today.minusDays(1)
            else -> return 0
        }
        var streak = 0
        while (reviewedDates.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}