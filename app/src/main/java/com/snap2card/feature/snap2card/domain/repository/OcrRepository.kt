package com.snap2card.feature.snap2card.domain.repository

import android.net.Uri
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates reviewable cards from a user-selected image or document source.
 */
interface OcrRepository {
    suspend fun generateCards(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>>
}

@Singleton
class FakeOcrRepositoryImpl @Inject constructor() : OcrRepository {
    override suspend fun generateCards(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>> {
        delay(1500)
        return Result.success(
            listOf(
                GeneratedVocabularyCard(
                    term = "Mitochondria",
                    definition = "The powerhouse of the cell",
                    translation = "mitochondrion",
                ),
                GeneratedVocabularyCard(
                    term = "Photosynthesis",
                    definition = "Process by which plants convert light into energy",
                    translation = "photosynthesis",
                ),
                GeneratedVocabularyCard(
                    term = "Osmosis",
                    definition = "Movement of water across a semi-permeable membrane",
                    translation = "osmosis",
                ),
            )
        )
    }
}
