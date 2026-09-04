package com.wanderlab.snap2card.controllers.serverconnection.implements.retrieve

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CardInfo
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CardListResponse
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
import com.wanderlab.snap2card.controllers.serverconnection.implements.toCardInfoList

internal class CardRetrieve(private val executor: ApiExecutor) {

    suspend fun listCards(): ApiResult<CardListResponse> =
        executor.execute(
            Endpoints.CARD_LIST
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            CardListResponse(
                numOfCard = data.getInt("numOfCard"),
                cards = data.getJSONArray("cards").toCardInfoList()
            )
        }

    suspend fun retrieveCards(ids: List<String>): ApiResult<List<CardInfo>> =
        executor.execute(
            Endpoints.CARD_RETRIEVE,
            query = ids.map { "ids" to it }
        ) { json ->
            val data = json.optJSONArray("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            data.toCardInfoList()
        }
}