package com.snap2card.feature.card_generation.domain.usecase

import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.card_generation.domain.repository.VocabularyGenerationRepository
import javax.inject.Inject

class GenerateVocabularyFromTextUseCase @Inject constructor(
    private val repository: VocabularyGenerationRepository,
) {
    suspend operator fun invoke(
        text: String,
        level: String = "B1",
        count: Int = 20,
        includePhrases: Boolean = true,
        sourceType: String = "scan",
    ): Result<List<GeneratedVocabularyCard>> = repository.generateVocabularyFromText(
        text = text,
        level = level,
        count = count,
        includePhrases = includePhrases,
        sourceType = sourceType,
    )
}
