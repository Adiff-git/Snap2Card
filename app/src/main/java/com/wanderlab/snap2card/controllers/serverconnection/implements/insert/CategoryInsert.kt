package com.wanderlab.snap2card.controllers.serverconnection.implements.insert

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CreateCategoryResponse
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import org.json.JSONArray

internal class CategoryInsert(private val executor: ApiExecutor) {

    suspend fun createCategory(name: String): ApiResult<CreateCategoryResponse> =
        executor.execute(
            Endpoints.CATEGORY_CREATE,
            body = executor.jsonBody {
                put("name", name)
            }
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            CreateCategoryResponse(categoryId = data.getString("categoryId"))
        }

    suspend fun categorizeCards(categoryId: String, cardIds: List<String>): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.CATEGORY_CATEGORIZE,
            body = executor.jsonBody {
                put("categoryId", categoryId)
                put("cardIds", JSONArray(cardIds))
            }
        ) {
            ApiSuccessResponse()
        }
}