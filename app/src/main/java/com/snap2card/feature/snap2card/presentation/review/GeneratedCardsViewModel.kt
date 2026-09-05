package com.snap2card.feature.snap2card.presentation.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.deck.domain.repository.DeckRepository
import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import com.snap2card.feature.snap2card.domain.vocabulary.repository.GeneratedVocabularyCardStore
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
    private val deckId: String? = savedStateHandle["deckId"]

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<GeneratedCardsUiState> = _uiState.asStateFlow()

    fun regenerate() {
        // TODO: Re-run the original source through the generation pipeline.
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
            _uiState.value = state.copy(saveError = "Fill in front and back sides for selected cards.")
            return
        }

        val savingState = state.copy(isSaving = true, saveError = null)
        _uiState.value = savingState

        viewModelScope.launch {
            try {
                val targetDeckId = deckId ?: run {
                    val deckTitle = state.deckName.ifBlank { "Generated Deck" }
                    deckRepository.createDeck(deckTitle, "Generated from ${state.category.lowercase()} vocabulary").id
                }
                for (card in selectedCards) {
                    deckRepository.addCard(targetDeckId, card.term, card.buildBackSide())
                }
                _uiState.value = GeneratedCardsUiState.Saved(deckId = targetDeckId, savedCount = selectedCards.size)
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

        return GeneratedCardsUiState.Error("Generated cards are no longer available. Please scan the image again.")
    }

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

}
