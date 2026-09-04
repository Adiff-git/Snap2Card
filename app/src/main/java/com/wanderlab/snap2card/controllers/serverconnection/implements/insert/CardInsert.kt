package com.wanderlab.snap2card.controllers.serverconnection.implements.insert

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CreateCardsResponse
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import com.wanderlab.snap2card.controllers.serverconnection.implements.toCreateCardsResponse
import org.json.JSONArray

internal class CardInsert(private val executor: ApiExecutor) {

    suspend fun createCard(frontSide: String, backSide: String): ApiResult<CreateCardsResponse> =
        executor.execute(
            Endpoints.CARD_CREATE,
            body = executor.jsonBody {
                put("frontSide", frontSide)
                put("backSide", backSide)
            }
        ) { json ->
            json.optJSONObject("data")?.toCreateCardsResponse(frontSide, backSide)
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
        }

    suspend fun createCardFromDocument(text: String): ApiResult<CreateCardsResponse> =
        executor.execute(
            Endpoints.CARD_CREATE_FROM_DOCUMENT,
            body = executor.textBody(text)
        ) { json ->
            json.optJSONObject("data")?.toCreateCardsResponse()
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
        }

    suspend fun createCardFromPdf(pdfBytes: ByteArray): ApiResult<CreateCardsResponse> =
        executor.execute(
            Endpoints.CARD_CREATE_FROM_PDF,
            body = executor.pdfBody(pdfBytes)
        ) { json ->
            json.optJSONObject("data")?.toCreateCardsResponse()
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
        }

    suspend fun categorizeCard(cardId: String, categoryIds: List<String>): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.CARD_CATEGORIZE,
            body = executor.jsonBody {
                put("cardId", cardId)
                put("categoryIds", JSONArray(categoryIds))
            }
        ) {
            ApiSuccessResponse()
        }
}