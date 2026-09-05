package com.snap2card.core.network.dto

import kotlinx.serialization.Serializable

/**
 * Every Snap2Card API response is wrapped in this envelope:
 * { "status": "success", "data": {...} }
 *
 * Retrofit/serialization maps straight to this, then callers unwrap `.data`.
 */
@Serializable
data class ApiResponse<T>(val status: String, val data: T)