package com.snap2card.feature.account.data.remote

import com.snap2card.core.network.dto.ApiResponse
import com.snap2card.feature.account.data.remote.dto.AccountResponse
import com.snap2card.feature.account.data.remote.dto.DailyLearnedCountDto
import com.snap2card.feature.account.data.remote.dto.MonthlyLearnedCountData
import retrofit2.http.GET
import retrofit2.http.Headers

interface AccountApiService {
    @Headers("Content-Type: application/json")
    @GET("account")
    suspend fun getAccount(): AccountResponse

    @Headers("Content-Type: application/json")
    @GET("account/monthly-learned-count")
    suspend fun getMonthlyLearnedCount(): ApiResponse<List<DailyLearnedCountDto>>
}
