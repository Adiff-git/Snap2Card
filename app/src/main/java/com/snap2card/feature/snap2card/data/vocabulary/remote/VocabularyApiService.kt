package com.snap2card.feature.snap2card.data.vocabulary.remote

import com.snap2card.feature.snap2card.data.vocabulary.remote.dto.VocabularyFromTextRequest
import com.snap2card.feature.snap2card.data.vocabulary.remote.dto.VocabularyGenerationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VocabularyApiService {
    @POST("vocabulary/from-text")
    suspend fun generateVocabularyFromText(
        @Body request: VocabularyFromTextRequest,
    ): VocabularyGenerationResponse

    @Multipart
    @POST("vocabulary/from-pdf")
    suspend fun generateVocabularyFromPdf(
        @Part file: MultipartBody.Part,
        @Part("level") level: RequestBody,
        @Part("count") count: RequestBody,
        @Part("includePhrases") includePhrases: RequestBody,
    ): VocabularyGenerationResponse
}
