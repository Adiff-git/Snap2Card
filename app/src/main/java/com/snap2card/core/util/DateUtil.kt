package com.snap2card.core.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import com.snap2card.feature.deck.data.remote.dto.ApiTimeDto
import java.time.OffsetDateTime
import java.time.ZoneOffset

object DateUtil {
    private val displayFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDate(timestamp: Long): String = displayFormat.format(Date(timestamp))
    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))
    fun now(): Long = System.currentTimeMillis()

    /** Parses a "yyyy-MM-dd" date string into epoch millis, start of day. */
    fun parseDate(date: String): Long = isoDateFormat.parse(date)?.time ?: 0L

    /** Parses an ISO-8601 timestamp (e.g. "2026-09-04T10:30:00Z") into epoch millis.
     * ASSUMPTION: some exam endpoints may return epoch millis as a number instead
     * of a string — check exam-review.md / category-log-related.md before trusting this. */
    fun parseTimestamp(timestamp: String): Long =
        try {
            Instant.parse(timestamp).toEpochMilli()
        } catch (e: DateTimeParseException) {
            0L
        }

    fun ApiTimeDto.toEpochMillis(): Long {
        val year = year ?: return now()
        val month = month ?: return now()
        val day = day ?: return now()
        val hour = hour ?: 0
        val minute = minute ?: 0
        val second = second ?: 0
        val offset = runCatching { gmt?.let(ZoneOffset::of) ?: ZoneOffset.UTC }.getOrDefault(ZoneOffset.UTC)
        return runCatching {
            OffsetDateTime.of(year, month, day, hour, minute, second, 0, offset)
                .toInstant()
                .toEpochMilli()
        }.getOrDefault(now())
    }
}
