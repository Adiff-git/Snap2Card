package com.snap2card.feature.snap2card.domain.usecase

import android.net.Uri
import com.snap2card.feature.snap2card.domain.model.GeneratedCard
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import javax.inject.Inject

/**
 * Submits an image and fetches the resulting generated card in one call,
 * so the ViewModel doesn't need to orchestrate the two-step API itself.
 *
 * Returns a single GeneratedCard now, not a List — POST /cards creates
 * exactly one card per call (confirmed: no batch endpoint exists).
 */
class UploadImageForOcrUseCase @Inject constructor(
    private val ocrRepository: OcrRepository,
) {
    suspend operator fun invoke(uri: Uri, mimeType: String, name: String): Result<GeneratedCard> {
        val submitResult = ocrRepository.submitImage(uri, mimeType, name)
        val cardId = submitResult.getOrElse { return Result.failure(it) }
        return ocrRepository.getGeneratedCard(cardId)
    }
}

/**
 * Same two-step chain, for the "Import Document" path (type = "document").
 */
class UploadDocumentForOcrUseCase @Inject constructor(
    private val ocrRepository: OcrRepository,
) {
    suspend operator fun invoke(text: String, name: String): Result<GeneratedCard> {
        val submitResult = ocrRepository.submitDocument(text, name)
        val cardId = submitResult.getOrElse { return Result.failure(it) }
        return ocrRepository.getGeneratedCard(cardId)
    }
}