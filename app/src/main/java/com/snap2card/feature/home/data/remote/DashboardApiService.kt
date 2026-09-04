package com.snap2card.feature.home.data.remote

import com.snap2card.feature.home.data.remote.dto.AccountDto
import com.snap2card.feature.home.data.remote.dto.AccountResponse
import com.snap2card.feature.home.data.remote.dto.DailyLearnedCountResponse
import com.snap2card.feature.home.data.remote.dto.MonthlyLearnedCountResponse
import com.snap2card.feature.home.data.remote.dto.RecentCategoriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for all Home-dashboard related backend endpoints.
 * Base URL: https://wanderlab2414.online/snap2card/api/v1.0/
 */
interface DashboardApiService {

    /** GET /account — user profile + daily goal */
    @GET("account")
    suspend fun getAccount(): AccountResponse

    /** GET /categories/recent?n=8 — recent decks with mastery */
    @GET("categories/recent")
    suspend fun getRecentCategories(
        @Query("n") n: Int = 8
    ): RecentCategoriesResponse

    /** GET /account/daily-learned-count — cards learned today */
    @GET("account/daily-learned-count")
    suspend fun getDailyLearnedCount(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): DailyLearnedCountResponse

    /** GET /account/monthly-learned-count — for streak calculation */
    @GET("account/monthly-learned-count")
    suspend fun getMonthlyLearnedCount(): MonthlyLearnedCountResponse
}
