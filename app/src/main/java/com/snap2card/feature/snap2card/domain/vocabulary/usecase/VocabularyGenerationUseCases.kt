package com.snap2card.feature.snap2card.domain.vocabulary.usecase

import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import com.snap2card.feature.snap2card.domain.vocabulary.model.VocabularyGenerationDefaults
import com.snap2card.feature.snap2card.domain.vocabulary.repository.VocabularyGenerationRepository
import javax.inject.Inject

class GenerateVocabularyFromTextUseCase @Inject constructor(
    private val repository: VocabularyGenerationRepository,
) {
    suspend operator fun invoke(
        text: String,
        level: String = VocabularyGenerationDefaults.LEVEL,
        count: Int = VocabularyGenerationDefaults.COUNT,
        includePhrases: Boolean = VocabularyGenerationDefaults.INCLUDE_PHRASES,
        sourceType: String = "scan",
    ): Result<List<GeneratedVocabularyCard>> = repository.generateVocabularyFromText(
        text = text,
        level = level,
        count = count,
        includePhrases = includePhrases,
        sourceType = sourceType,
    )
}
