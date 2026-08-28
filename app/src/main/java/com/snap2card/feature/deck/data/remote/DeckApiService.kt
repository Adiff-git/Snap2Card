package com.snap2card.feature.deck.data.remote

import com.snap2card.feature.deck.data.remote.dto.CardDto
import com.snap2card.feature.deck.data.remote.dto.CreateDeckRequest
import com.snap2card.feature.deck.data.remote.dto.DeckDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeckApiService {
    @GET("decks")
    suspend fun getDecks(): List<DeckDto>

    @GET("decks/{deckId}")
    suspend fun getDeckById(@Path("deckId") deckId: String): DeckDto

    @POST("decks")
    suspend fun createDeck(@Body request: CreateDeckRequest): DeckDto

    @DELETE("decks/{deckId}")
    suspend fun deleteDeck(@Path("deckId") deckId: String)

    @GET("decks/{deckId}/cards")
    suspend fun getCardsForDeck(@Path("deckId") deckId: String): List<CardDto>
}
