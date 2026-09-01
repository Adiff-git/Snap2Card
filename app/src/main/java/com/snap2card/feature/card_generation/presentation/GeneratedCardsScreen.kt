package com.snap2card.feature.card_generation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.feedback.ErrorState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.feature.deck.presentation.editor.DeckEditorScaffold

@Composable
fun GeneratedCardsScreen(
    onNavigateBack: () -> Unit,
    onDeckSaved: (deckId: String) -> Unit,
    viewModel: GeneratedCardsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is GeneratedCardsUiState.Loading -> LoadingIndicator()
        is GeneratedCardsUiState.Error -> ErrorState(message = state.message)
        is GeneratedCardsUiState.Success -> DeckEditorScaffold(
            topBarTitle = "Review & Edit",
            title = if (state.jobId == "manual") "Manual Cards" else "Generated Cards (${state.cards.size})",
            subtitle = "",
            titleTag = state.category,
            saveText = "Save Cards to Deck",
            initialDeckName = "Generated Deck",
            initialTag = state.category,
            initialCards = state.cards,
            showDeckInfo = false,
            cardsSectionTitle = null,
            secondaryActionText = if (state.jobId == "manual") null else "Regenerate",
            onSecondaryAction = viewModel::regenerate,
            onNavigateBack = onNavigateBack,
            onSave = { result ->
                val deckId = result.deckName.ifBlank { "generated-${state.jobId}" }
                onDeckSaved(deckId)
            },
        )
    }
}
