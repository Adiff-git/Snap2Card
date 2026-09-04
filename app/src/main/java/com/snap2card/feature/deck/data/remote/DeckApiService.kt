package com.snap2card.feature.deck.data.remote

import com.snap2card.feature.deck.data.remote.dto.CardCategorizeRequest
import com.snap2card.feature.deck.data.remote.dto.CardCategorizeResponse
import com.snap2card.feature.deck.data.remote.dto.CardCreateRequest
import com.snap2card.feature.deck.data.remote.dto.CardCreateResponse
import com.snap2card.feature.deck.data.remote.dto.CardListResponse
import com.snap2card.feature.deck.data.remote.dto.CardRetrieveRequest
import com.snap2card.feature.deck.data.remote.dto.CardRetrieveResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryCreateRequest
import com.snap2card.feature.deck.data.remote.dto.CategoryCreateResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryListResponse
import com.snap2card.feature.deck.data.remote.dto.CategoryRetrieveRequest
import com.snap2card.feature.deck.data.remote.dto.CategoryRetrieveResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface DeckApiService {
    @GET("categories/list")
    suspend fun getCategoryList(): CategoryListResponse

    @GET("categories")
    suspend fun getCategory(@Query("id") id: String): CategoryRetrieveResponse

    @GET("cards/list")
    suspend fun getCardList(): CardListResponse

    // ASSUMPTION: no card-retrieve.md yet — following the same query-param convention
    // just confirmed for categories. Confirm field name and list format (repeated param
    // vs comma-joined string) before trusting this.
    @HTTP(method = "GET", path = "cards", hasBody = true)
    suspend fun getCards(@Body request: CardRetrieveRequest): CardRetrieveResponse

    @POST("cards")
    suspend fun createCard(@Body request: CardCreateRequest): CardCreateResponse

    @POST("categories")
    suspend fun createCategory(@Body request: CategoryCreateRequest): CategoryCreateResponse

    @POST("cards/categorize")
    suspend fun categorizeCard(@Body request: CardCategorizeRequest): CardCategorizeResponse
}
