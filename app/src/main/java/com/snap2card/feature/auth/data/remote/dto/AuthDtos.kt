package com.snap2card.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Google OAuth (kept for future use) ─────────────────────────────────────

@Serializable
data class GoogleAuthRequest(
    @SerialName("id_token") val idToken: String,
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user_id") val userId: String,
    @SerialName("email") val email: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("photo_url") val photoUrl: String? = null,
)

// ── Email/Password Login (current auth flow) ───────────────────────────────

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

/**
 * Backend returns: {"data":{"token":"SESS00000000001"}}
 */
@Serializable
data class LoginResponse(
    val data: LoginData,
)

@Serializable
data class LoginData(
    val token: String,
)
