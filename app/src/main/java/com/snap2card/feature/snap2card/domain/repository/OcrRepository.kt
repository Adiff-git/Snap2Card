package com.snap2card.feature.snap2card.domain.repository

import android.net.Uri
import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard

/**
 * Generates reviewable cards from a user-selected image or document source.
 */
interface OcrRepository {
    suspend fun generateCards(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>>
}
