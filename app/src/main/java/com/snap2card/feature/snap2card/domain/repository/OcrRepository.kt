package com.snap2card.feature.snap2card.domain.repository

import android.net.Uri
import com.snap2card.feature.snap2card.domain.model.GeneratedCard

/**
 * OCR repository interface.
 * Hides all backend details — Android has no knowledge of OCR/AI implementation.
 * The network call, multipart encoding, and DTO mapping happen in the implementation.
 */
interface OcrRepository {
    /**
     * Upload an image/document URI and receive generated flashcards.
     * @param uri     Content URI from camera or file picker
     * @param mimeType MIME type of the file
     */
    suspend fun uploadAndProcess(uri: Uri, mimeType: String): Result<List<GeneratedCard>>
}
