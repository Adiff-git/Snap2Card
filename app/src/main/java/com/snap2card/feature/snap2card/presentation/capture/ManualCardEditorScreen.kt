package com.snap2card.feature.snap2card.presentation.capture

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Indigo100
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.Spacing
import com.snap2card.feature.deck.presentation.editor.DeckEditorCardInput

@Composable
fun ManualCardEditorScreen(
    onNavigateBack: () -> Unit,
    onStudy: (String) -> Unit,
    onReview: (String) -> Unit,
    viewModel: ManualCardEditorViewModel = hiltViewModel(),
    deckId: String,
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedCardCount = uiState.savedCardCount

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = if (savedCardCount == null) "Add Cards" else "Cards Saved",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        },
        bottomBar = {
            if (savedCardCount == null) {
                ManualCardEditorBottomBar(
                    saveText = uiState.saveText,
                    enabled = uiState.isValid && !uiState.isSaving,
                    onSave = viewModel::save,
                )
            }
        },
    ) { padding ->
        if (savedCardCount != null) {
            ManualCardSavedContent(
                savedCardCount = savedCardCount,
                onStudy = { onStudy(deckId) },
                onReview = { onReview(deckId) },
                modifier = Modifier.padding(padding),
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            itemsIndexed(uiState.cards) { index, card ->
                ManualFlashcardEditor(
                    index = index,
                    card = card,
                    canDelete = uiState.cards.size > 1,
                    showValidation = uiState.showValidation,
                    onFrontChange = { viewModel.updateFront(index, it) },
                    onBackChange = { viewModel.updateBack(index, it) },
                    onDelete = { viewModel.deleteCard(index) },
                )
            }

            item {
                OutlinedButton(
                    onClick = viewModel::addCard,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo500),
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Add another card", modifier = Modifier.padding(start = Spacing.xs))
                }
            }

            if (!uiState.isValid) {
                item {
                    Text(
                        text = "Fill in front and back for every card to save.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualCardSavedContent(
    savedCardCount: Int,
    onStudy: () -> Unit,
    onReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Cards saved",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "$savedCardCount ${if (savedCardCount == 1) "card" else "cards"} added to this deck.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.lg))
        PrimaryButton(
            text = "Study",
            onClick = onStudy,
        )
        Spacer(Modifier.height(Spacing.sm))
        SecondaryButton(
            text = "Review",
            onClick = onReview,
        )
    }
}

@Composable
private fun ManualFlashcardEditor(
    index: Int,
    card: DeckEditorCardInput,
    canDelete: Boolean,
    showValidation: Boolean,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, Indigo100, MaterialTheme.shapes.medium)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Card ${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = onDelete,
                    enabled = canDelete,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete card",
                        tint = if (canDelete) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Text(
                "Front",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            ManualTextField(
                value = card.front,
                onValueChange = onFrontChange,
                placeholder = "Enter term or question...",
                singleLine = true,
                minHeight = 54.dp,
                isError = showValidation && card.front.isBlank(),
            )

            Text(
                "Back",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            ManualTextField(
                value = card.back,
                onValueChange = onBackChange,
                placeholder = "Enter definition or answer...",
                singleLine = false,
                minHeight = 112.dp,
                isError = showValidation && card.back.isBlank(),
            )
        }
    }
}

@Composable
private fun ManualTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    minHeight: Dp,
    isError: Boolean,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(minHeight),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        singleLine = singleLine,
        isError = isError,
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
        ),
    )
}

@Composable
private fun ManualCardEditorBottomBar(
    saveText: String,
    enabled: Boolean,
    onSave: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        PrimaryButton(
            text = saveText,
            onClick = onSave,
            enabled = enabled,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
        )
    }
}
