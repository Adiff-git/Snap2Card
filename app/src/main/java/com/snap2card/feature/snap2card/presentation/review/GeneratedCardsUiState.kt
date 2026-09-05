package com.snap2card.feature.snap2card.presentation.review

data class GeneratedCardReviewItem(
    val id: String,
    val term: String,
    val definition: String,
    val translation: String,
    val partOfSpeech: String? = null,
    val example: String? = null,
    val sourceSentence: String? = null,
    val difficulty: String? = null,
    val selected: Boolean = true,
) {
    val isValid: Boolean = term.isNotBlank() && definition.isNotBlank() && translation.isNotBlank()

    fun buildBackSide(): String = listOfNotNull(
        "Definition: $definition",
        "Translation: $translation",
        partOfSpeech?.takeIf { it.isNotBlank() }?.let { "Part of speech: $it" },
        example?.takeIf { it.isNotBlank() }?.let { "Example: $it" },
        sourceSentence?.takeIf { it.isNotBlank() }?.let { "Source: $it" },
        difficulty?.takeIf { it.isNotBlank() }?.let { "Difficulty: $it" },
    ).joinToString(separator = "\n")
}

sealed class GeneratedCardsUiState {
    data object Loading : GeneratedCardsUiState()
    data class Success(
        val jobId: String,
        val category: String,
        val deckName: String = "Generated Deck",
        val cards: List<GeneratedCardReviewItem>,
        val canRegenerate: Boolean = false,
        val isSaving: Boolean = false,
        val saveError: String? = null,
    ) : GeneratedCardsUiState()
    data class Saved(val deckId: String, val savedCount: Int) : GeneratedCardsUiState()
    data class Error(val message: String) : GeneratedCardsUiState()
}
