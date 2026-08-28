package com.snap2card.feature.settings.domain.model

data class UserSettings(
    val dailyGoalCards: Int = 20,
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en",
)
