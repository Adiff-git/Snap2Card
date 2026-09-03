package com.snap2card.feature.card_generation.domain.model

data class GeneratedVocabularyCard(
    val term: String,
    val definition: String,
    val translation: String,
    val partOfSpeech: String? = null,
    val example: String? = null,
    val sourceSentence: String? = null,
    val difficulty: String? = null,
)
