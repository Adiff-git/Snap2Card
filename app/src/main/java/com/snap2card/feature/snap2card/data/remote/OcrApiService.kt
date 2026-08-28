package com.snap2card.feature.snap2card.data.remote

import com.snap2card.feature.snap2card.data.remote.dto.OcrUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrApiService {
    /**
     * Upload an image or document for OCR + AI processing.
     * The backend returns a list of generated flashcards.
     */
    @Multipart
    @POST("ocr/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): OcrUploadResponse
}
