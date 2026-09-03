package com.snap2card.feature.card_generation.domain.repository

import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard

interface VocabularyGenerationRepository {
    suspend fun generateVocabularyFromText(
        text: String,
        level: String = "B1",
        count: Int = 20,
        includePhrases: Boolean = true,
        sourceType: String = "scan",
    ): Result<List<GeneratedVocabularyCard>>
}
