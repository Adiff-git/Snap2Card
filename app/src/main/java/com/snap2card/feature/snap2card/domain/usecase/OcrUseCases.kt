package com.snap2card.feature.snap2card.domain.usecase

import android.net.Uri
import com.snap2card.feature.snap2card.domain.model.OcrResult
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import javax.inject.Inject

class ExtractTextForOcrUseCase @Inject constructor(
    private val ocrRepository: OcrRepository,
) {
    suspend operator fun invoke(uri: Uri): Result<OcrResult> = ocrRepository.extractText(uri)
}

class GenerateCardsFromOcrTextUseCase @Inject constructor(
    private val ocrRepository: OcrRepository,
) {
    suspend operator fun invoke(text: String, sourceType: String = "scan"): Result<List<GeneratedVocabularyCard>> =
        ocrRepository.generateCardsFromText(text, sourceType)
}

/**
 * Generates reviewable vocabulary cards from a captured image or imported document.
 */
class UploadImageForOcrUseCase @Inject constructor(
    private val ocrRepository: OcrRepository,
) {
    suspend operator fun invoke(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>> =
        ocrRepository.generateCards(uri, mimeType)
}
