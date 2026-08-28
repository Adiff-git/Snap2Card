package com.snap2card.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    private val displayFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun formatDate(timestamp: Long): String = displayFormat.format(Date(timestamp))
    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))
    fun now(): Long = System.currentTimeMillis()
}
