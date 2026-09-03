package com.snap2card.feature.card_generation.data.mapper

import com.snap2card.feature.card_generation.data.remote.dto.GeneratedVocabularyCardDto
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.deck.data.remote.dto.CardCreateRequest

fun GeneratedVocabularyCardDto.toDomain() = GeneratedVocabularyCard(
    term = term,
    definition = definition,
    translation = translation,
    partOfSpeech = partOfSpeech,
    example = example,
    sourceSentence = sourceSentence,
    difficulty = difficulty,
)

fun List<GeneratedVocabularyCardDto>.toDomain() = map { it.toDomain() }

fun GeneratedVocabularyCard.toCardCreateRequest() = CardCreateRequest(
    name = term.take(60).ifBlank { "Vocabulary Card" },
    type = "manual",
    frontSide = term,
    backSide = buildVocabularyBackSide(),
)

fun GeneratedVocabularyCard.buildVocabularyBackSide(): String = listOfNotNull(
    "Definition: $definition",
    "Translation: $translation",
    partOfSpeech?.takeIf { it.isNotBlank() }?.let { "Part of speech: $it" },
    example?.takeIf { it.isNotBlank() }?.let { "Example: $it" },
    sourceSentence?.takeIf { it.isNotBlank() }?.let { "Source: $it" },
    difficulty?.takeIf { it.isNotBlank() }?.let { "Difficulty: $it" },
).joinToString(separator = "\n")
