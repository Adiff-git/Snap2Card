package com.snap2card.feature.card_generation.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VocabularyFromTextRequest(
    @SerialName("text") val text: String,
    @SerialName("level") val level: String = "B1",
    @SerialName("count") val count: Int = 20,
    @SerialName("includePhrases") val includePhrases: Boolean = true,
    @SerialName("sourceType") val sourceType: String = "scan",
)

@Serializable
data class VocabularyGenerationResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: VocabularyGenerationData,
)

@Serializable
data class VocabularyGenerationData(
    @SerialName("source") val source: VocabularyGenerationSourceDto,
    @SerialName("cards") val cards: List<GeneratedVocabularyCardDto>,
)

@Serializable
data class VocabularyGenerationSourceDto(
    @SerialName("type") val type: String,
)

@Serializable
data class GeneratedVocabularyCardDto(
    @SerialName("term") val term: String,
    @SerialName("definition") val definition: String,
    @SerialName("translation") val translation: String,
    @SerialName("partOfSpeech") val partOfSpeech: String? = null,
    @SerialName("example") val example: String? = null,
    @SerialName("sourceSentence") val sourceSentence: String? = null,
    @SerialName("difficulty") val difficulty: String? = null,
)
