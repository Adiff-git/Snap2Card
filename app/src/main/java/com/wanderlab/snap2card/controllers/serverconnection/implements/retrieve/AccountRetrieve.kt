package com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.AccountResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.DailyLearnedCountResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.MonthlyLearnedEntry
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import com.wanderlab.snap2card.controllers.serverconnection.implements.toTime

internal class AccountRetrieve(private val executor: ApiExecutor) {

    suspend fun getAccount(): ApiResult<AccountResponse> =
        executor.execute(
            Endpoints.ACCOUNT_RETRIEVE
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            AccountResponse(
                email = data.getString("email"),
                name = data.getString("name"),
                phone = data.getString("phone"),
                dailyGoal = data.getInt("dailyGoal"),
                createdAt = data.getJSONObject("createdAt").toTime()
            )
        }

    suspend fun getAvatar(): ApiResult<ByteArray> =
        executor.executeRaw(
            Endpoints.ACCOUNT_AVATAR_RETRIEVE
        ) { response ->
            response.body?.bytes()
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Empty avatar response")
        }

    suspend fun getDailyLearnedCount(
        year: Int,
        month: Int,
        day: Int
    ): ApiResult<DailyLearnedCountResponse> =
        executor.execute(
            Endpoints.ACCOUNT_DAILY_LEARNED_COUNT,
            query = listOf(
                "year" to year.toString(),
                "month" to month.toString(),
                "day" to day.toString()
            )
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            DailyLearnedCountResponse(count = data.getInt("count"))
        }

    suspend fun getMonthlyLearnedCount(): ApiResult<List<MonthlyLearnedEntry>> =
        executor.execute(
            Endpoints.ACCOUNT_MONTHLY_LEARNED_COUNT
        ) { json ->
            val data = json.optJSONArray("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            buildList {
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    add(
                        MonthlyLearnedEntry(
                            day = item.getString("day"),
                            cardCount = item.getInt("cardCount")
                        )
                    )
                }
            }
        }
}