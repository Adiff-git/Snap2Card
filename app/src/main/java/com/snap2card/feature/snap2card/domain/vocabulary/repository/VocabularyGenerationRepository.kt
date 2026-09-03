package com.snap2card.feature.snap2card.domain.vocabulary.repository

import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import com.snap2card.feature.snap2card.domain.vocabulary.model.VocabularyGenerationDefaults

interface VocabularyGenerationRepository {
    suspend fun generateVocabularyFromText(
        text: String,
        level: String = VocabularyGenerationDefaults.LEVEL,
        count: Int = VocabularyGenerationDefaults.COUNT,
        includePhrases: Boolean = VocabularyGenerationDefaults.INCLUDE_PHRASES,
        sourceType: String = "scan",
    ): Result<List<GeneratedVocabularyCard>>
}
