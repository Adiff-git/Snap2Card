package com.snap2card.feature.home.data.repository

import com.snap2card.feature.home.domain.model.DashboardData
import com.snap2card.feature.home.domain.model.RecentDeck
import com.snap2card.feature.home.domain.repository.DashboardRepository
import com.snap2card.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake implementation of [DashboardRepository] providing realistic mock data
 * for development and UI testing. Replace with a real API-backed implementation
 * once the backend endpoints are available.
 */
@Singleton
class FakeDashboardRepository @Inject constructor(
    private val settingsRepository: SettingsRepository
) : DashboardRepository {

    override suspend fun getDashboard(): DashboardData {
        val settings = settingsRepository.getSettings().first()
        return DashboardData(
            userName = "Scholar",
            userPhotoUrl = null,
            streakCount = 7,
            recentDecks = listOf(
                RecentDeck(
                    id = "1",
                    title = "Biology Basics",
                    category = "Science",
                    cardCount = 45,
                    masteryPercent = 0.72f,
                ),
                RecentDeck(
                    id = "2",
                    title = "Japanese N5 Vocabulary",
                    category = "Language",
                    cardCount = 120,
                    masteryPercent = 0.45f,
                ),
                RecentDeck(
                    id = "3",
                    title = "World War II Timeline",
                    category = "History",
                    cardCount = 30,
                    masteryPercent = 0.90f,
                ),
                RecentDeck(
                    id = "4",
                    title = "Kotlin Coroutines",
                    category = "Programming",
                    cardCount = 28,
                    masteryPercent = 0.60f,
                ),
            ),
            dailyGoalTotal = settings.dailyGoalCards,
            dailyGoalCompleted = minOf(25, settings.dailyGoalCards),
        )
    }
}
