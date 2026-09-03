package com.snap2card.feature.card_generation.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.card_generation.domain.repository.GeneratedVocabularyCardStore
import com.snap2card.feature.deck.domain.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratedCardsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val generatedVocabularyCardStore: GeneratedVocabularyCardStore,
    private val deckRepository: DeckRepository,
) : ViewModel() {

    private val jobId: String = savedStateHandle["jobId"] ?: "local"

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<GeneratedCardsUiState> = _uiState.asStateFlow()

    fun regenerate() {
        _uiState.value = GeneratedCardsUiState.Success(
            jobId = jobId,
            category = "Medical",
            canRegenerate = true,
            cards = sampleCards(),
        )
    }

    fun updateDeckName(value: String) {
        updateSuccess { it.copy(deckName = value, saveError = null) }
    }

    fun toggleCardSelection(cardId: String) {
        updateCard(cardId) { it.copy(selected = !it.selected) }
    }

    fun selectAll() {
        updateSuccess { state -> state.copy(cards = state.cards.map { it.copy(selected = true) }, saveError = null) }
    }

    fun deselectAll() {
        updateSuccess { state -> state.copy(cards = state.cards.map { it.copy(selected = false) }, saveError = null) }
    }

    fun updateTerm(cardId: String, value: String) {
        updateCard(cardId) { it.copy(term = value) }
    }

    fun updateDefinition(cardId: String, value: String) {
        updateCard(cardId) { it.copy(definition = value) }
    }

    fun updateTranslation(cardId: String, value: String) {
        updateCard(cardId) { it.copy(translation = value) }
    }

    fun deleteCard(cardId: String) {
        updateSuccess { state ->
            state.copy(
                cards = state.cards.filterNot { it.id == cardId },
                saveError = null,
            )
        }
    }

    fun addSelectedCardsToDeck() {
        val state = _uiState.value as? GeneratedCardsUiState.Success ?: return
        if (state.isSaving) return

        val selectedCards = state.cards.filter { it.selected }
        if (selectedCards.isEmpty()) {
            _uiState.value = state.copy(saveError = "Select at least one card to add to a deck.")
            return
        }
        if (selectedCards.any { !it.isValid }) {
            _uiState.value = state.copy(saveError = "Fill in term, definition, and translation for selected cards.")
            return
        }

        val savingState = state.copy(isSaving = true, saveError = null)
        _uiState.value = savingState

        viewModelScope.launch {
            try {
                val deckTitle = state.deckName.ifBlank { "Generated Deck" }
                val deck = deckRepository.createDeck(deckTitle, "Generated from ${state.category.lowercase()} vocabulary")
                for (card in selectedCards) {
                    deckRepository.addCard(deck.id, card.term, card.buildBackSide())
                }
                _uiState.value = GeneratedCardsUiState.Saved(deckId = deck.id, savedCount = selectedCards.size)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.value = savingState.copy(
                    isSaving = false,
                    saveError = error.message ?: "Could not add cards to deck. Try again.",
                )
            }
        }
    }

    private fun initialState(): GeneratedCardsUiState {
        generatedVocabularyCardStore.get(jobId)?.let { cards ->
            return GeneratedCardsUiState.Success(
                jobId = jobId,
                category = "Scan",
                cards = cards.map { card ->
                    card.toReviewItem()
                },
            )
        }

        return if (jobId == "manual" || jobId == "local") {
            sampleState()
        } else {
            GeneratedCardsUiState.Error("Generated cards are no longer available. Please scan the image again.")
        }
    }

    private fun sampleState(): GeneratedCardsUiState = GeneratedCardsUiState.Success(
        jobId = jobId,
        category = if (jobId == "manual") "Manual" else "Medical",
        canRegenerate = jobId != "manual",
        cards = if (jobId == "manual") emptyList() else sampleCards(),
    )

    private fun updateCard(cardId: String, transform: (GeneratedCardReviewItem) -> GeneratedCardReviewItem) {
        updateSuccess { state ->
            state.copy(
                cards = state.cards.map { if (it.id == cardId) transform(it) else it },
                saveError = null,
            )
        }
    }

    private fun updateSuccess(transform: (GeneratedCardsUiState.Success) -> GeneratedCardsUiState.Success) {
        _uiState.update { state ->
            if (state is GeneratedCardsUiState.Success && !state.isSaving) transform(state) else state
        }
    }

    private fun GeneratedVocabularyCard.toReviewItem(): GeneratedCardReviewItem = GeneratedCardReviewItem(
        id = "generated-${term.lowercase()}-${hashCode()}",
        term = term,
        definition = definition,
        translation = translation,
        partOfSpeech = partOfSpeech,
        example = example,
        sourceSentence = sourceSentence,
        difficulty = difficulty,
    )

    private fun sampleCards(): List<GeneratedCardReviewItem> = listOf(
        GeneratedCardReviewItem(
            id = "sample-mitochondria",
            term = "Mitochondria",
            definition = "The part of a cell that produces energy.",
            translation = "ty thể",
            partOfSpeech = "noun",
        ),
        GeneratedCardReviewItem(
            id = "sample-nucleus",
            term = "Nucleus",
            definition = "The central part of a cell that contains genetic material.",
            translation = "nhân tế bào",
            partOfSpeech = "noun",
        ),
        GeneratedCardReviewItem(
            id = "sample-ribosome",
            term = "Ribosome",
            definition = "A cell structure that makes proteins.",
            translation = "ribosome",
            partOfSpeech = "noun",
        ),
    )
}
