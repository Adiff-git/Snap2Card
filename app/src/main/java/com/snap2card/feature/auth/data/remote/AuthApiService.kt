package com.snap2card.feature.auth.data.remote

import com.snap2card.feature.auth.data.remote.dto.AuthResponse
import com.snap2card.feature.auth.data.remote.dto.GoogleAuthRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/google")
    suspend fun signInWithGoogle(@Body request: GoogleAuthRequest): AuthResponse
}
