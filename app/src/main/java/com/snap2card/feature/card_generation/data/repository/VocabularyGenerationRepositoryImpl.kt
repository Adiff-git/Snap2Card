package com.snap2card.feature.card_generation.data.repository

import com.snap2card.feature.card_generation.data.mapper.toDomain
import com.snap2card.feature.card_generation.data.remote.VocabularyApiService
import com.snap2card.feature.card_generation.data.remote.dto.VocabularyFromTextRequest
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.card_generation.domain.repository.VocabularyGenerationRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
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
    ): Result<List<GeneratedVocabularyCard>> = try {
        Result.success(
            vocabularyApiService.generateVocabularyFromText(
                VocabularyFromTextRequest(
                    text = text,
                    level = level,
                    count = count,
                    includePhrases = includePhrases,
                    sourceType = sourceType,
                )
            ).data.cards.toDomain()
        )
    } catch (error: Throwable) {
        if (error is CancellationException) throw error
        Result.failure(error.toVocabularyGenerationFailure())
    }

    private fun Throwable.toVocabularyGenerationFailure(): Throwable {
        if (this !is HttpException) {
            return IllegalStateException("Could not generate cards. Check your connection and try again.", this)
        }

        val apiMessage = response()
            ?.errorBody()
            ?.string()
            ?.extractApiMessage()

        val message = buildString {
            append("Vocabulary service returned HTTP ")
            append(code())
            if (!apiMessage.isNullOrBlank()) {
                append(": ")
                append(apiMessage)
            }
        }
        return IllegalStateException(message, this)
    }

    private fun String.extractApiMessage(): String? = runCatching {
        Json.parseToJsonElement(this)
            .jsonObject["message"]
            ?.jsonPrimitive
            ?.contentOrNull
    }.getOrNull()
}
