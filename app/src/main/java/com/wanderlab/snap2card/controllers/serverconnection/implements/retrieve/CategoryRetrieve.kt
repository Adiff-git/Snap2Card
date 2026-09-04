package com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CategoryDetail
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CategoryListResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ExamLogEntry
import com.wanderlab.snap2card.controllers.serverconnection.definitions.RecentCategoryEntry
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import com.wanderlab.snap2card.controllers.serverconnection.implements.toCategorySummaryList
import com.wanderlab.snap2card.controllers.serverconnection.implements.toTime
import com.wanderlab.snap2card.controllers.serverconnection.implements.toStringList

internal class CategoryRetrieve(private val executor: ApiExecutor) {

    suspend fun listCategories(): ApiResult<CategoryListResponse> =
        executor.execute(
            Endpoints.CATEGORY_LIST
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            CategoryListResponse(
                categoryNum = data.getInt("categoryNum"),
                categories = data.getJSONArray("categories").toCategorySummaryList()
            )
        }

    suspend fun retrieveCategory(id: String): ApiResult<CategoryDetail> =
        executor.execute(
            Endpoints.CATEGORY_RETRIEVE,
            query = listOf("id" to id)
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            CategoryDetail(
                name = data.getString("name"),
                numOfCard = data.getInt("numOfCard"),
                mastery = if (data.isNull("mastery")) null else data.optDouble("mastery"),
                createdAt = data.getJSONObject("createdAt").toTime(),
                cardIds = data.getJSONArray("cardIds").toStringList()
            )
        }

    suspend fun getCategoryLogs(categoryId: String): ApiResult<List<ExamLogEntry>> =
        executor.execute(
            Endpoints.CATEGORY_LOGS,
            query = listOf("categoryId" to categoryId)
        ) { json ->
            val data = json.optJSONArray("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            buildList {
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    add(
                        ExamLogEntry(
                            logId = item.getString("logId"),
                            examName = item.getString("examName"),
                            score = item.getInt("score"),
                            totalScore = item.getInt("totalScore"),
                            start = item.getJSONObject("start").toTime(),
                            end = item.getJSONObject("end").toTime()
                        )
                    )
                }
            }
        }

    suspend fun getRecentCategories(n: Int?): ApiResult<List<RecentCategoryEntry>> =
        executor.execute(
            Endpoints.CATEGORY_RECENT,
            query = n?.let { listOf("n" to it.toString()) } ?: emptyList()
        ) { json ->
            val data = json.optJSONArray("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            buildList {
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    add(
                        RecentCategoryEntry(
                            categoryId = item.getString("categoryId"),
                            name = item.getString("name"),
                            mastery = if (item.isNull("mastery")) null else item.optDouble("mastery"),
                            lastTakenAt = item.getJSONObject("lastTakenAt").toTime()
                        )
                    )
                }
            }
        }
}