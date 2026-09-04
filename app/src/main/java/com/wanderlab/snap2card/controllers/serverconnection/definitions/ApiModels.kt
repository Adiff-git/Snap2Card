package com.wanderlab.snap2card.controllers.serverconnection.definitions

data class Time(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val gmt: String
)

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
}

object ApiStatus {
    const val SUCCESS = "success"
    const val ERROR = "error"
}

object ApiErrorCode {
    const val BAD_REQUEST = 400
    const val UNAUTHORIZED = 401
    const val NOT_FOUND = 404
    const val CONFLICT = 409
    const val UNSUPPORTED_MEDIA_TYPE = 415
    const val UNPROCESSABLE_ENTITY = 422
    const val VERSION_MISMATCH = 426
    const val INTERNAL_SERVER_ERROR = 500
    const val VOCABULARY_GENERATION_FAILED = 502
    const val VOCABULARY_UNAVAILABLE = 503
}

class ApiError(val code: Int, override val message: String) : Exception(message)

data class ApiSuccessResponse(val status: String = "success")

data class ServerErrorResponse(
    val status: String,
    val message: String
)