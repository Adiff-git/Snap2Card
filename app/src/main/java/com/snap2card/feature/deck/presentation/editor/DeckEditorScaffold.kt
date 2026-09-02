package com.snap2card.feature.deck.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.Indigo100
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.InputBackground
import com.snap2card.design_system.theme.MedicalTagBackground
import com.snap2card.design_system.theme.MedicalTagText
import com.snap2card.design_system.theme.Spacing

data class DeckEditorCardInput(
    val front: String = "",
    val back: String = "",
)

data class DeckEditorResult(
    val deckName: String,
    val tag: String,
    val cards: List<DeckEditorCardInput>,
)

@Composable
fun DeckEditorScaffold(
    topBarTitle: String = "Review & Edit",
    title: String,
    subtitle: String,
    titleTag: String? = null,
    saveText: String,
    initialDeckName: String = "",
    initialTag: String = "Medical",
    initialCards: List<DeckEditorCardInput> = listOf(DeckEditorCardInput()),
    showDeckInfo: Boolean = true,
    cardsSectionTitle: String? = "Cards",
    addCardText: String = "Add Empty Card",
    validateBeforeSave: Boolean = false,
    validationMessage: String = "Fill in front and back for every card to save.",
    saveTextForCount: ((Int) -> String)? = null,
    subtitleForCount: ((Int) -> String)? = null,
    secondaryActionText: String? = null,
    onSecondaryAction: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onSave: (DeckEditorResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    var deckName by remember { mutableStateOf(initialDeckName) }
    var tag by remember { mutableStateOf(initialTag) }
    val cards = remember { mutableStateListOf(*initialCards.toTypedArray()) }
    val cardsAreValid = cards.isNotEmpty() && cards.all { it.front.isNotBlank() && it.back.isNotBlank() }
    val effectiveSaveText = saveTextForCount?.invoke(cards.size) ?: saveText
    val effectiveSubtitle = subtitleForCount?.invoke(cards.size) ?: subtitle

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            DeckEditorTopBar(title = topBarTitle, onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            DeckEditorBottomBar(
                saveText = effectiveSaveText,
                secondaryActionText = secondaryActionText,
                onSecondaryAction = onSecondaryAction,
                saveEnabled = !validateBeforeSave || cardsAreValid,
                onSave = {
                    if (!validateBeforeSave || cardsAreValid) {
                        onSave(
                            DeckEditorResult(
                                deckName = deckName,
                                tag = tag,
                                cards = cards.toList(),
                            )
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    if (titleTag != null) {
                        Surface(shape = MaterialTheme.shapes.extraLarge, color = MedicalTagBackground) {
                            Text(
                                titleTag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MedicalTagText,
                                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                            )
                        }
                    }
                }
                if (effectiveSubtitle.isNotBlank()) {
                    Text(
                        effectiveSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xs),
                    )
                }
            }
            if (showDeckInfo) {
                item {
                    DeckNameCard(
                        deckName = deckName,
                        onDeckNameChange = { deckName = it },
                        tag = tag,
                        onAddTag = { tag = if (tag.isBlank()) "Medical" else tag },
                    )
                }
            }
            if (cardsSectionTitle != null) {
                item {
                    Text(cardsSectionTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
            itemsIndexed(cards) { index, card ->
                ManualCardEditor(
                    index = index,
                    card = card,
                    canDelete = cards.size > 1,
                    showValidation = validateBeforeSave,
                    onFrontChange = { cards[index] = card.copy(front = it) },
                    onBackChange = { cards[index] = card.copy(back = it) },
                    onDelete = { cards.removeAt(index) },
                )
            }
            item {
                OutlinedButton(
                    onClick = { cards.add(DeckEditorCardInput()) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo500),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(addCardText, modifier = Modifier.padding(start = Spacing.xs))
                }
            }
            if (validateBeforeSave && !cardsAreValid) {
                item {
                    Text(
                        text = validationMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeckEditorTopBar(title: String, onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Indigo500,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DeckNameCard(
    deckName: String,
    onDeckNameChange: (String) -> Unit,
    tag: String,
    onAddTag: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text("Deck Name", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            FlatTextField(
                value = deckName,
                onValueChange = onDeckNameChange,
                placeholder = "e.g., Biology 101 Midterm",
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TextButton(
                    onClick = onAddTag,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.extraLarge),
                ) {
                    Text("+ Add Tag", style = MaterialTheme.typography.labelSmall)
                }
                if (tag.isNotBlank()) {
                    Surface(shape = MaterialTheme.shapes.extraLarge, color = MedicalTagBackground) {
                        Text(
                            tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MedicalTagText,
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ManualCardEditor(
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
                Text("Card ${index + 1}", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.weight(1f))
                if (canDelete) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete card", modifier = Modifier.size(18.dp))
                    }
                }
            }
            Text("Front (Term)", style = MaterialTheme.typography.labelSmall)
            FlatTextField(
                value = card.front,
                onValueChange = onFrontChange,
                placeholder = "Enter term or question...",
                minHeight = 72.dp,
                isError = showValidation && card.front.isBlank(),
            )
            Text("Back (Definition)", style = MaterialTheme.typography.labelSmall)
            FlatTextField(
                value = card.back,
                onValueChange = onBackChange,
                placeholder = "Enter definition or answer...",
                minHeight = 96.dp,
                isError = showValidation && card.back.isBlank(),
            )
        }
    }
}

@Composable
private fun FlatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = false,
    minHeight: androidx.compose.ui.unit.Dp = 54.dp,
    isError: Boolean = false,
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
private fun DeckEditorBottomBar(
    saveText: String,
    secondaryActionText: String?,
    onSecondaryAction: () -> Unit,
    saveEnabled: Boolean,
    onSave: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (secondaryActionText != null) {
                OutlinedButton(
                    onClick = onSecondaryAction,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.weight(0.75f),
                ) {
                    Text(secondaryActionText)
                }
                Spacer(Modifier.size(Spacing.sm))
                PrimaryButton(
                    text = saveText,
                    onClick = onSave,
                    enabled = saveEnabled,
                    modifier = Modifier.weight(1.45f),
                )
            } else {
                PrimaryButton(
                    text = saveText,
                    onClick = onSave,
                    enabled = saveEnabled,
                    modifier = Modifier.fillMaxWidth(0.72f),
                )
            }
        }
    }
}
