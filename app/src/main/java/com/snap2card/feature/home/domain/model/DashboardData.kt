package com.snap2card.feature.home.domain.model

/**
 * Domain model for the Home dashboard aggregate data.
 * Combines user info, recent decks, and daily goal metrics
 * into a single object for the UI layer.
 */
data class DashboardData(
    val userName: String,
    val userPhotoUrl: String?,
    val streakCount: Int,
    val recentDecks: List<RecentDeck>,
    val dailyGoalTotal: Int,
    val dailyGoalCompleted: Int,
)

/**
 * Lightweight deck representation for the Home screen horizontal list.
 * Includes category and mastery progress not present in the base Deck model.
 */
data class RecentDeck(
    val id: String,
    val title: String,
    val category: String,
    val cardCount: Int,
    val masteryPercent: Float,  // 0.0 – 1.0
)
