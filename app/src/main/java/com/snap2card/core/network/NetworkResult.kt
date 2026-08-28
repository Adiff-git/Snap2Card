package com.snap2card.core.network

/**
 * Wraps remote API responses to avoid exposing exceptions to the domain layer.
 * Repository implementations map NetworkResult → domain models.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(
        val code: Int? = null,
        val message: String,
        val throwable: Throwable? = null
    ) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/** Convenience: execute a suspend call and wrap in NetworkResult. */
suspend fun <T> safeApiCall(call: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(call())
    } catch (e: Exception) {
        NetworkResult.Error(message = e.localizedMessage ?: "Unknown error", throwable = e)
    }
}
