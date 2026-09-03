package com.snap2card.feature.card_generation.data.repository

import com.snap2card.feature.card_generation.data.mapper.toDomain
import com.snap2card.feature.card_generation.data.remote.VocabularyApiService
import com.snap2card.feature.card_generation.data.remote.dto.VocabularyFromTextRequest
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.card_generation.domain.repository.VocabularyGenerationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyGenerationRepositoryImpl @Inject constructor(
    private val vocabularyApiService: VocabularyApiService,
) : VocabularyGenerationRepository {
    override suspend fun generateVocabularyFromText(
        text: String,
        level: String,
        count: Int,
        includePhrases: Boolean,
        sourceType: String,
    ): Result<List<GeneratedVocabularyCard>> = runCatching {
        vocabularyApiService.generateVocabularyFromText(
            VocabularyFromTextRequest(
                text = text,
                level = level,
                count = count,
                includePhrases = includePhrases,
                sourceType = sourceType,
            )
        ).data.cards.toDomain()
    }
}
