package com.snap2card.feature.deck.presentation.create

import androidx.compose.runtime.Composable
import com.snap2card.feature.deck.presentation.editor.DeckEditorScaffold

/** Create New Deck screen. Developer B owns this. */
@Composable
fun CreateDeckScreen(
    onDeckCreated: (deckId: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    DeckEditorScaffold(
        title = "Create New Deck",
        subtitle = "Choose how you want to add cards to your study deck.",
        saveText = "Save Deck",
        initialDeckName = "",
        initialTag = "",
        onNavigateBack = onNavigateBack,
        onSave = { onDeckCreated("preview-deck") },
    )
}
