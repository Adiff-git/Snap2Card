package com.snap2card.feature.deck.data.remote

import com.snap2card.feature.deck.data.remote.dto.CardCategorizeRequest
import com.snap2card.feature.deck.data.remote.dto.CardCategorizeResponse
import com.snap2card.feature.deck.data.remote.dto.CardCreateRequest
import com.snap2card.feature.deck.data.remote.dto.CardCreateResponse
import com.snap2card.feature.deck.data.remote.dto.CardEditResponse
import com.snap2card.feature.deck.data.remote.dto.CardListResponse
import com.snap2card.feature.deck.data.remote.dto.CardRetrieveResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryCreateRequest
import com.snap2card.feature.deck.data.remote.dto.CategoryCreateResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryDeleteResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryListResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryRetrieveResponse
import com.snap2card.feature.snap2card.data.remote.dto.CardEditRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface DeckApiService {
    @Headers("Content-Type: application/json")
    @GET("categories/list")
    suspend fun getCategoryList(): CategoryListResponse

    @Headers("Content-Type: application/json")
    @GET("categories")
    suspend fun getCategory(@Query("id") id: String): CategoryRetrieveResponse

    @Headers("Content-Type: application/json")
    @GET("cards/list")
    suspend fun getCardList(): CardListResponse

    @Headers("Content-Type: application/json")
    @GET("cards")
    suspend fun getCards(@Query("ids") ids: String): CardRetrieveResponse

    @POST("cards")
    suspend fun createCard(@Body request: CardCreateRequest): CardCreateResponse

    @POST("categories")
    suspend fun createCategory(@Body request: CategoryCreateRequest): CategoryCreateResponse

    @DELETE("categories")
    suspend fun deleteCategory(@Query("id") id: String): CategoryDeleteResponse

    @POST("cards/categorize")
    suspend fun categorizeCard(@Body request: CardCategorizeRequest): CardCategorizeResponse

    @PUT("cards")
    suspend fun updateCard(@Body request: CardEditRequest): CardEditResponse
}
