package com.wanderlab.snap2card.controllers.serverconnection.implements.update

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditCardRequest
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import org.json.JSONArray

internal class CardUpdate(private val executor: ApiExecutor) {

    suspend fun editCard(request: EditCardRequest): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.CARD_EDIT,
            body = executor.jsonBody {
                put("id", request.id)
                request.frontSide?.let { put("frontSide", it) }
                request.backSide?.let { put("backSide", it) }
                request.categories?.let { put("categories", JSONArray(it)) }
            }
        ) {
            ApiSuccessResponse()
        }

    suspend fun deleteCard(id: String): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.CARD_DELETE,
            query = listOf("id" to id)
        ) {
            ApiSuccessResponse()
        }
}