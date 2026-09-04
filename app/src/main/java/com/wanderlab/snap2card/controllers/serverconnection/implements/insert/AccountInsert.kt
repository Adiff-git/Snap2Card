package com.wanderlab.snap2card.controllers.serverconnection.implements.insert

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig.Endpoints
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiSuccessResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.LoginResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.RegisterResponse
import com.wanderlab.snap2card.controllers.serverconnection.implements.ApiExecutor
internal class AccountInsert(private val executor: ApiExecutor) {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> =
        executor.execute(
            Endpoints.ACCOUNT_LOGIN,
            body = executor.jsonBody {
                put("email", email)
                put("password", password)
            }
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            executor.token = data.getString("token")
            LoginResponse(token = data.getString("token"))
        }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): ApiResult<RegisterResponse> =
        executor.execute(
            Endpoints.ACCOUNT_REGISTER,
            body = executor.jsonBody {
                put("name", name)
                put("email", email)
                put("phone", phone)
                put("password", password)
            }
        ) { json ->
            val data = json.optJSONObject("data")
                ?: throw ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR, "Missing data in response")
            RegisterResponse(accountId = data.getString("accountId"))
        }

    suspend fun logout(): ApiResult<ApiSuccessResponse> =
        executor.execute(
            Endpoints.ACCOUNT_LOGOUT,
            body = executor.jsonBody { }
        ) {
            executor.token = null
            ApiSuccessResponse()
        }
}