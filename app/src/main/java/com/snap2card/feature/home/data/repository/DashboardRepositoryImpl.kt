package com.snap2card.feature.home.data.repository

import com.snap2card.feature.home.data.mapper.computeStreak
import com.snap2card.feature.home.data.mapper.toRecentDeck
import com.snap2card.feature.deck.data.local.dao.CardDao
import com.snap2card.feature.deck.data.remote.DeckApiService
import com.snap2card.feature.home.data.remote.DashboardApiService
import com.snap2card.feature.home.domain.model.DashboardData
import com.snap2card.feature.home.domain.repository.DashboardRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real implementation of [DashboardRepository].
 * Aggregates data from:
 *  - GET /account              → user name + daily goal
 *  - GET /categories/recent    → recent decks with mastery %
 *  - GET /account/daily-learned-count   → cards studied today
 *  - GET /account/monthly-learned-count → streak calculation
 */
@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val api: DashboardApiService,
    private val deckApiService: DeckApiService,
    private val cardDao: CardDao,
) : DashboardRepository {

    override suspend fun getDashboard(): DashboardData {
        // 1. User profile + daily goal
        val accountResponse = api.getAccount()
        val account = accountResponse.data

        // 2. Recent decks (with mastery %)
        val recentResponse = api.getRecentCategories(n = 8)
        val categoryCounts = runCatching {
            deckApiService.getCategoryList().data.categories.associate { category ->
                category.id to (category.numOfCard ?: 0)
            }
        }.getOrElse { error ->
            if (error is CancellationException) throw error
            emptyMap()
        }
        val recentDecks = recentResponse.data.map { category ->
            val remoteCardCount = categoryCounts[category.categoryId] ?: 0
            val localCardCount = cardDao.getCardCount(category.categoryId)
            category.toRecentDeck(cardCount = maxOf(remoteCardCount, localCardCount))
        }

        // 3. Daily learned count
        val today = java.time.LocalDate.now()
        val dailyResponse = api.getDailyLearnedCount(
            year = today.year,
            month = today.monthValue,
            day = today.dayOfMonth
        )
        val dailyCompleted = dailyResponse.data.count

        // 4. Monthly learned count → streak
        val monthlyResponse = api.getMonthlyLearnedCount()
        val streak = computeStreak(monthlyResponse.data)

        return DashboardData(
            userName = account.name,
            userPhotoUrl = null,            // avatar handled separately if needed
            streakCount = streak,
            recentDecks = recentDecks,
            dailyGoalTotal = account.dailyGoal,
            dailyGoalCompleted = dailyCompleted,
        )
    }
}
