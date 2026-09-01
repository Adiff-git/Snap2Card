package com.snap2card.feature.card_generation.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GeneratedCardsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val jobId: String = savedStateHandle["jobId"] ?: "local"

    private val _uiState = MutableStateFlow(sampleState())
    val uiState: StateFlow<GeneratedCardsUiState> = _uiState.asStateFlow()

    fun regenerate() {
        _uiState.value = GeneratedCardsUiState.Success(
            jobId = jobId,
            category = "Medical",
            cards = listOf(
                DeckEditorCardInput(
                    front = "Cell membrane",
                    back = "A selectively permeable barrier that surrounds the cell and controls movement in and out.",
                ),
                DeckEditorCardInput(
                    front = "Cytoplasm",
                    back = "The gel-like fluid inside the cell where organelles are suspended and reactions occur.",
                ),
                DeckEditorCardInput(
                    front = "ATP",
                    back = "The primary energy-carrying molecule used by cells to power biological processes.",
                ),
            ),
        )
    }

    private fun sampleState(): GeneratedCardsUiState = GeneratedCardsUiState.Success(
        jobId = jobId,
        category = "Medical",
        cards = listOf(
            DeckEditorCardInput(
                front = "Mitochondria",
                back = "The powerhouse of the cell, responsible for generating most of the cell's supply of adenosine triphosphate.",
            ),
            DeckEditorCardInput(
                front = "Nucleus",
                back = "A membrane-bound organelle found in eukaryotic cells that contains the cell's genetic material.",
            ),
            DeckEditorCardInput(
                front = "Ribosome",
                back = "A complex macromolecular machine found within all living cells that performs biological protein synthesis.",
            ),
        ),
    )
}
