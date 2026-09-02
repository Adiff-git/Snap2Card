package com.snap2card.feature.deck.data.remote

import com.snap2card.feature.deck.data.remote.dto.CardCreateRequest
import com.snap2card.feature.deck.data.remote.dto.CardCreateResponse
import com.snap2card.feature.deck.data.remote.dto.CardListResponse
import com.snap2card.feature.deck.data.remote.dto.CardRetrieveRequest
import com.snap2card.feature.deck.data.remote.dto.CardRetrieveResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryListResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryRetrieveRequest
import com.snap2card.feature.deck.data.remote.dto.CategoryRetrieveResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface DeckApiService {
    @GET("categories/list")
    suspend fun getCategoryList(): CategoryListResponse

    @HTTP(method = "GET", path = "categories", hasBody = true)
    suspend fun getCategory(@Body request: CategoryRetrieveRequest): CategoryRetrieveResponse

    @GET("cards/list")
    suspend fun getCardList(): CardListResponse

    @HTTP(method = "GET", path = "cards", hasBody = true)
    suspend fun getCards(@Body request: CardRetrieveRequest): CardRetrieveResponse

    @POST("cards")
    suspend fun createCard(@Body request: CardCreateRequest): CardCreateResponse
}
