package com.wanderlab.snap2card.controllers.serverconnection.implements.update

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditCategoryRequest
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor

internal class CategoryUpdate(private val executor: ApiExecutor) {

    suspend fun editCategory(request: EditCategoryRequest): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.CATEGORY_EDIT,
            body = executor.jsonBody {
                put("id", request.id)
                request.name?.let { put("name", it) }
            }
        ) {
            ApiSuccessResponse()
        }

    suspend fun deleteCategory(id: String): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.CATEGORY_DELETE,
            query = listOf("id" to id)
        ) {
            ApiSuccessResponse()
        }
}