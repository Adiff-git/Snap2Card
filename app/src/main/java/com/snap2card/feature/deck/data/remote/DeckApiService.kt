package com.snap2card.feature.deck.data.remote

import com.snap2card.feature.deck.data.remote.dto.CategoryListResponse
import retrofit2.http.GET

interface DeckApiService {
    @GET("categories/list")
    suspend fun getCategoryList(): CategoryListResponse
}
