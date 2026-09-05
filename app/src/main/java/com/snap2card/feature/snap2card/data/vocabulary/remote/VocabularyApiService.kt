package com.snap2card.feature.snap2card.data.vocabulary.remote

import com.snap2card.feature.snap2card.data.vocabulary.remote.dto.VocabularyFromTextRequest
import com.snap2card.feature.snap2card.data.vocabulary.remote.dto.VocabularyGenerationResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VocabularyApiService {
    @POST("vocabulary/from-text")
    suspend fun generateVocabularyFromText(
        @Body request: VocabularyFromTextRequest,
    ): VocabularyGenerationResponse

    @POST("vocabulary/from-pdf")
    suspend fun generateVocabularyFromPdf(
        @Body file: RequestBody,
        @Query("level") level: String,
        @Query("count") count: Int,
        @Query("includePhrases") includePhrases: Boolean,
    ): VocabularyGenerationResponse
}
