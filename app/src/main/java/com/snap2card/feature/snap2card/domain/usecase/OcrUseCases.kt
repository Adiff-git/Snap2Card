package com.snap2card.feature.snap2card.domain.usecase

import android.net.Uri
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import javax.inject.Inject

/**
 * Generates reviewable vocabulary cards from a captured image or imported document.
 */
class UploadImageForOcrUseCase @Inject constructor(
    private val ocrRepository: OcrRepository,
) {
    suspend operator fun invoke(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>> =
        ocrRepository.generateCards(uri, mimeType)
}
