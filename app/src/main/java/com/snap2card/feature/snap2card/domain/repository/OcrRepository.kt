package com.snap2card.feature.snap2card.domain.repository

import android.net.Uri
import com.snap2card.feature.snap2card.domain.model.GeneratedCard
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

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

@Singleton
class FakeOcrRepositoryImpl @Inject constructor() : OcrRepository {
    override suspend fun uploadAndProcess(uri: Uri, mimeType: String): Result<List<GeneratedCard>> {
        delay(1500) // simulate network + AI processing time
        return Result.success(
            listOf(
                GeneratedCard(front = "Mitochondria", back = "The powerhouse of the cell"),
                GeneratedCard(front = "Photosynthesis", back = "Process by which plants convert light into energy"),
                GeneratedCard(front = "Osmosis", back = "Movement of water across a semi-permeable membrane"),
            )
        )
    }
}
// For demo purposes