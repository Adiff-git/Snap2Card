package com.snap2card.feature.deck.presentation.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.feedback.ErrorState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput
import com.snap2card.feature.deck.presentation.editor.DeckEditorScaffold

/** Review and edit a generated/existing deck before saving. */
@Composable
fun EditDeckScreen(
    deckId: String,
    onNavigateBack: () -> Unit,
    onDeckSaved: (deckId: String) -> Unit,
    onStudyClick: (String) -> Unit,
    viewModel: EditDeckViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.saveResult.collect { result ->
            when (result) {
                is EditDeckViewModel.SaveResult.Success -> onDeckSaved(deckId)
                is EditDeckViewModel.SaveResult.Error -> {
                    // surface a snackbar/toast here instead of silently swallowing it
                }
            }
        }
    }


    when (val state = uiState) {
        EditDeckUiState.Loading -> LoadingIndicator(message = "Loading deck...")
        is EditDeckUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::retry)
        is EditDeckUiState.Success -> DeckEditorScaffold(
            topBarTitle = "Deck Details",
            title = state.deck.title,
            subtitle = "${state.cards.size} ${if (state.cards.size == 1) "Card" else "Cards"}",
            saveText = "Done",
            initialDeckName = state.deck.title,
            initialTag = state.deck.description,
            initialCards = state.cards.map { card ->
                DeckEditorCardInput(id = card.id, front = card.front, back = card.back)
            },
            showDeckInfo = false,
            cardsSectionTitle = null,
            subtitleForCount = { count -> "$count ${if (count == 1) "Card" else "Cards"}" },
            onNavigateBack = onNavigateBack,
            onSave = { result -> viewModel.saveChanges(result) },
            onDeleteCard = { card -> card.id?.let(viewModel::deleteCard) },
            secondaryActionText = "Study",
            onSecondaryAction = { onStudyClick(deckId) },
        )
    }
}
