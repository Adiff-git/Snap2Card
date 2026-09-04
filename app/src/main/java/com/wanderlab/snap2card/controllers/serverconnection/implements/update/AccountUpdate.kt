package com.wanderlab.snap2card.controllers.serverconnection.implements.update

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditAccountRequest
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor

internal class AccountUpdate(private val executor: ApiExecutor) {

    suspend fun updateAvatar(
        imageBytes: ByteArray,
        mimeType: String
    ): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.ACCOUNT_AVATAR_UPDATE,
            body = executor.bytesBody(imageBytes, mimeType)
        ) {
            ApiSuccessResponse()
        }

    suspend fun editAccount(request: EditAccountRequest): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.ACCOUNT_EDIT,
            body = executor.jsonBody {
                put("type", request.type)
                request.name?.let { put("name", it) }
                request.email?.let { put("email", it) }
                request.phone?.let { put("phone", it) }
                request.dailyGoal?.let { put("dailyGoal", it) }
            }
        ) {
            ApiSuccessResponse()
        }
}