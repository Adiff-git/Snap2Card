package com.snap2card.feature.deck.presentation.edit

import androidx.compose.runtime.Composable
import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput
import com.snap2card.feature.deck.presentation.editor.DeckEditorScaffold

/** Review and edit a generated/existing deck before saving. */
@Composable
fun EditDeckScreen(
    deckId: String,
    onNavigateBack: () -> Unit,
    onDeckSaved: (deckId: String) -> Unit,
) {
    DeckEditorScaffold(
        topBarTitle = "Review & Edit",
        title = "Generated Cards (3)",
        subtitle = "",
        titleTag = "Medical",
        saveText = "Save Deck",
        initialDeckName = "Mitochondria",
        initialTag = "Medical",
        initialCards = listOf(
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
        showDeckInfo = false,
        showSourceOptions = false,
        cardsSectionTitle = null,
        secondaryActionText = "Regenerate",
        onNavigateBack = onNavigateBack,
        onSave = { _ -> onDeckSaved(deckId) },
    )
}
