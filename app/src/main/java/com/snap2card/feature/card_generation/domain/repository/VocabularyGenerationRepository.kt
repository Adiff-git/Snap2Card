package com.snap2card.feature.card_generation.domain.repository

import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.card_generation.domain.model.VocabularyGenerationDefaults

interface VocabularyGenerationRepository {
    suspend fun generateVocabularyFromText(
        text: String,
        level: String = VocabularyGenerationDefaults.LEVEL,
        count: Int = VocabularyGenerationDefaults.COUNT,
        includePhrases: Boolean = VocabularyGenerationDefaults.INCLUDE_PHRASES,
        sourceType: String = "scan",
    ): Result<List<GeneratedVocabularyCard>>
}
