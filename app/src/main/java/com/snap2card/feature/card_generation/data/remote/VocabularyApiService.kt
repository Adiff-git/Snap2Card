package com.snap2card.feature.card_generation.data.remote

import com.snap2card.feature.card_generation.data.remote.dto.VocabularyFromTextRequest
import com.snap2card.feature.card_generation.data.remote.dto.VocabularyGenerationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface VocabularyApiService {
    @POST("vocabulary/from-text")
    suspend fun generateVocabularyFromText(
        @Body request: VocabularyFromTextRequest,
    ): VocabularyGenerationResponse
}
