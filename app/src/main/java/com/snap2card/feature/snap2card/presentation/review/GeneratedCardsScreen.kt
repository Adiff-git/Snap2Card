package com.snap2card.feature.snap2card.presentation.review

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.feedback.ErrorState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.Indigo100
import com.snap2card.design_system.theme.Spacing

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
        is GeneratedCardsUiState.Saved -> {
            LaunchedEffect(state.deckId) { onDeckSaved(state.deckId) }
            LoadingIndicator(message = "Added ${state.savedCount} ${if (state.savedCount == 1) "card" else "cards"}. Opening deck...")
        }
        is GeneratedCardsUiState.Success -> GeneratedCardsReviewContent(
            state = state,
            onNavigateBack = onNavigateBack,
            onDeckNameChange = viewModel::updateDeckName,
            onToggleSelection = viewModel::toggleCardSelection,
            onSelectAll = viewModel::selectAll,
            onDeselectAll = viewModel::deselectAll,
            onTermChange = viewModel::updateTerm,
            onDefinitionChange = viewModel::updateDefinition,
            onTranslationChange = viewModel::updateTranslation,
            onDeleteCard = viewModel::deleteCard,
            onSave = viewModel::addSelectedCardsToDeck,
            onRegenerate = viewModel::regenerate,
        )
    }
}

@Composable
private fun GeneratedCardsReviewContent(
    state: GeneratedCardsUiState.Success,
    onNavigateBack: () -> Unit,
    onDeckNameChange: (String) -> Unit,
    onToggleSelection: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onTermChange: (String, String) -> Unit,
    onDefinitionChange: (String, String) -> Unit,
    onTranslationChange: (String, String) -> Unit,
    onDeleteCard: (String) -> Unit,
    onSave: () -> Unit,
    onRegenerate: () -> Unit,
) {
    val selectedCount = state.cards.count { it.selected }
    val selectedCardsValid = state.cards.filter { it.selected }.all { it.isValid }
    val canSave = selectedCount > 0 && selectedCardsValid && !state.isSaving

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            AppTopBar(
                title = "Review Cards",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    if (state.saveError != null) {
                        Text(
                            text = state.saveError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    PrimaryButton(
                        text = if (state.isSaving) "Adding cards to deck..." else "Add $selectedCount ${if (selectedCount == 1) "Card" else "Cards"} to Deck",
                        onClick = onSave,
                        enabled = canSave,
                    )
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item {
                Header(
                    state = state,
                    selectedCount = selectedCount,
                    onDeckNameChange = onDeckNameChange,
                    onSelectAll = onSelectAll,
                    onDeselectAll = onDeselectAll,
                    onRegenerate = onRegenerate,
                )
            }

            if (state.cards.isEmpty()) {
                item { EmptyGeneratedCardsState() }
            } else {
                items(state.cards, key = { it.id }) { card ->
                    GeneratedCardEditor(
                        card = card,
                        onToggleSelection = { onToggleSelection(card.id) },
                        onTermChange = { onTermChange(card.id, it) },
                        onDefinitionChange = { onDefinitionChange(card.id, it) },
                        onTranslationChange = { onTranslationChange(card.id, it) },
                        onDelete = { onDeleteCard(card.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    state: GeneratedCardsUiState.Success,
    selectedCount: Int,
    onDeckNameChange: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onRegenerate: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = "Generated Cards",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "$selectedCount selected / ${state.cards.size} total",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = state.deckName,
            onValueChange = onDeckNameChange,
            label = { Text("Deck name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            SecondaryButton(text = "Select All", onClick = onSelectAll, modifier = Modifier.weight(1f))
            SecondaryButton(text = "Deselect All", onClick = onDeselectAll, modifier = Modifier.weight(1f))
        }
        if (state.canRegenerate) {
            SecondaryButton(text = "Regenerate", onClick = onRegenerate)
        }
    }
}

@Composable
private fun GeneratedCardEditor(
    card: GeneratedCardReviewItem,
    onToggleSelection: () -> Unit,
    onTermChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onTranslationChange: (String) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, Indigo100, MaterialTheme.shapes.large)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = card.selected, onCheckedChange = { onToggleSelection() })
                Text(
                    text = if (card.selected) "Selected" else "Not selected",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove generated card")
                }
            }
            OutlinedTextField(
                value = card.term,
                onValueChange = onTermChange,
                label = { Text("Term") },
                isError = card.selected && card.term.isBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = card.definition,
                onValueChange = onDefinitionChange,
                label = { Text("Definition") },
                minLines = 2,
                isError = card.selected && card.definition.isBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = card.translation,
                onValueChange = onTranslationChange,
                label = { Text("Translation") },
                isError = card.selected && card.translation.isBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
            if (!card.example.isNullOrBlank()) {
                Text(
                    text = "Example: ${card.example}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EmptyGeneratedCardsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No generated cards left",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Go back and generate cards again, or keep reviewing if you add cards later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
