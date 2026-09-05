package com.snap2card.feature.history.domain.model

data class StudyActivity(
    val currentStreakDays: Int,
    val cardsThisMonth: Int,
    val dailyCounts: List<DayCount>, // for the heatmap grid
)

data class DayCount(
    val date: Long, // epoch millis, start of day
    val count: Int,
)