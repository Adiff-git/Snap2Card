package com.snap2card.feature.home.data.mapper

import com.snap2card.feature.home.data.remote.dto.MonthlyLearnedCountItemDto
import com.snap2card.feature.home.data.remote.dto.RecentCategoryItemDto
import com.snap2card.feature.home.domain.model.RecentDeck
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE

/**
 * Map a recent-category DTO to the RecentDeck domain model.
 * mastery from backend is 0–100 (percent); we convert to 0.0–1.0 Float.
 */
fun RecentCategoryItemDto.toRecentDeck(cardCount: Int = 0): RecentDeck = RecentDeck(
    id = categoryId,
    title = name,
    category = "Deck",                             // backend does not supply a category label here
    cardCount = cardCount,
    masteryPercent = ((mastery ?: 0.0) / 100.0).toFloat().coerceIn(0f, 1f)
)

/**
 * Compute streak in days from the monthly-learned-count list.
 * Counts consecutive days (ending today or yesterday) that have cardCount > 0.
 */
fun computeStreak(items: List<MonthlyLearnedCountItemDto>): Int {
    val activeDates = items
        .filter { it.cardCount > 0 }
        .mapNotNull { runCatching { LocalDate.parse(it.day, isoFormatter) }.getOrNull() }
        .sortedDescending()

    if (activeDates.isEmpty()) return 0

    val today = LocalDate.now()
    // Streak must start on today or yesterday
    if (activeDates.first() < today.minusDays(1)) return 0

    var streak = 0
    var cursor: LocalDate? = null
    for (date in activeDates) {
        if (cursor == null) {
            streak = 1
            cursor = date
        } else if (cursor.minusDays(1) == date) {
            streak++
            cursor = date
        } else break
    }
    return streak
}
