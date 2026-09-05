package com.snap2card.feature.history.data.mapper

import com.snap2card.core.util.DateUtil
import com.snap2card.core.util.DateUtil.toEpochMillis
import com.snap2card.feature.account.data.remote.dto.DailyLearnedCountDto
import com.snap2card.feature.account.data.remote.dto.MonthlyLearnedCountData
import com.snap2card.feature.deck.data.remote.dto.CategoryLogDto
import com.snap2card.feature.history.domain.model.DayCount
import com.snap2card.feature.history.domain.model.HistorySession
import com.snap2card.feature.history.domain.model.SessionStatus
import com.snap2card.feature.history.domain.model.StudyActivity
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar



private fun calculateStreak(dailyCounts: List<DayCount>): Int {
    val today = startOfDay(System.currentTimeMillis())
    val countByDay = dailyCounts.associate { it.date to it.count }
    var streak = 0
    var cursor = today
    while ((countByDay[cursor] ?: 0) > 0) {
        streak++
        cursor -= DAY_MILLIS
    }
    return streak
    // NOTE: truncates at month boundary until dailyCounts spans into last month —
    // same caveat flagged earlier for the monthly-learned-count scoping issue.
}

private fun startOfDay(millis: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private const val DAY_MILLIS = 24 * 60 * 60 * 1000L

fun CategoryLogDto.toDomain(categoryName: String) = HistorySession(
    id = logId,
    title = "$examName — $categoryName", // ASSUMPTION: no categoryName in response; combining with the caller's known category. Adjust display format as you like.
    cardsReviewed = totalScore ?: 0, // ASSUMPTION: no "cards reviewed" field exists — using totalScore as a stand-in count. CONFIRM with backend whether totalScore == number of cards, or if it's purely a scoring metric unrelated to card count.
    completedAt = end?.toEpochMillis() ?: DateUtil.now(),
    status = SessionStatus.COMPLETED, // doc explicitly says this endpoint only returns "completed exam logs" — no per-item status field to check
)

fun List<DailyLearnedCountDto>.toDomain(): StudyActivity {
    val dailyCounts = map { dto ->
        // Doc specifies "day" as YYYY-MM-DD, but live responses return a full
        // ISO timestamp (e.g. "2026-08-31T17:00:00.0"), often with a non-zero
        // time component. This looks like a server-side Date serialization bug
        // (possibly a GMT+7 local-midnight value serialized as UTC, shifting
        // the date back by one). Taking just the date substring for now —
        // this may be off by one day until backend confirms/fixes. FLAGGED.
        val datePart = dto.day.substringBefore("T")
        val epochMillis = LocalDate.parse(datePart)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        DayCount(date = epochMillis, count = dto.cardCount ?: 0)
    }
    return StudyActivity(
        currentStreakDays = calculateStreak(dailyCounts),
        cardsThisMonth = dailyCounts.sumOf { it.count },
        dailyCounts = dailyCounts,
    )
}

fun List<DailyLearnedCountDto>.toDailyCounts(): List<DayCount> = map { dto ->
    // Doc specifies "day" as YYYY-MM-DD, but live responses return a full
    // ISO timestamp (e.g. "2026-08-31T17:00:00.0"), often with a non-zero
    // time component. This looks like a server-side Date serialization bug
    // (possibly a GMT+7 local-midnight value serialized as UTC, shifting
    // the date back by one). Taking just the date substring for now —
    // this may be off by one day until backend confirms/fixes. FLAGGED.
    val datePart = dto.day.substringBefore("T")
    val epochMillis = LocalDate.parse(datePart)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
    DayCount(date = epochMillis, count = dto.cardCount ?: 0)
}
