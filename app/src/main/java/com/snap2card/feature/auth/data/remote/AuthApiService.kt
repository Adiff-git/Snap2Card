package com.snap2card.feature.auth.data.remote

import com.snap2card.feature.auth.data.remote.dto.AuthResponse
import com.snap2card.feature.auth.data.remote.dto.GoogleAuthRequest
import com.snap2card.feature.auth.data.remote.dto.LoginRequest
import com.snap2card.feature.auth.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    /** Email/password login — current auth flow */
    @POST("account/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /** Logout – ends the current session */
    @POST("account/logout")
    suspend fun logout(): Unit

    /** Google OAuth login — future use */
    @POST("auth/google")
    suspend fun signInWithGoogle(@Body request: GoogleAuthRequest): AuthResponse
}
