package com.snap2card.feature.snap2card.data.remote

import com.snap2card.core.network.dto.ApiResponse
import com.snap2card.feature.snap2card.data.remote.dto.CardCreateData
import com.snap2card.feature.snap2card.data.remote.dto.CardCreateRequest
import com.snap2card.feature.snap2card.data.remote.dto.CardDto
import com.snap2card.feature.snap2card.data.remote.dto.CardEditRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service for /cards.
 * Auth header (Bearer token) is expected to be attached by an OkHttp interceptor
 * in NetworkModule, not passed manually here — confirm that's already wired up
 * before relying on it.
 *
 * Base URL must be: https://wanderlab2414.online/snap2card/api/v1.0/
 */
interface CardApiService {

    @POST("cards")
    suspend fun createCard(@Body request: CardCreateRequest): ApiResponse<CardCreateData>

    /**
     * GET /cards?ids=CARD1&ids=CARD2
     * NOTE: the API doc doesn't specify whether repeated `ids` params or a
     * comma-joined single param is expected. Retrofit's default with
     * @Query("ids") List<String> sends repeated params (?ids=A&ids=B).
     * If the server expects "?ids=A,B" instead, switch to a single
     * @Query("ids") ids: String and join with "," yourself before calling.
     * Verify against a real response once backend is live.
     */
    @GET("cards")
    suspend fun getCards(@Query("ids") ids: List<String>): ApiResponse<List<CardDto>>

    @PUT("cards")
    suspend fun editCard(@Body request: CardEditRequest): ApiResponse<Unit?>
}