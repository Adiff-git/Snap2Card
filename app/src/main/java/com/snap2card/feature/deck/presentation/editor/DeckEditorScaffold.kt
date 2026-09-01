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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.BiologyTagBackground
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

@Composable
fun DeckEditorScaffold(
    topBarTitle: String = "Snap2Card",
    title: String,
    subtitle: String,
    titleTag: String? = null,
    saveText: String,
    initialDeckName: String = "",
    initialTag: String = "Medical",
    initialCards: List<DeckEditorCardInput> = listOf(DeckEditorCardInput()),
    showDeckInfo: Boolean = true,
    showSourceOptions: Boolean = true,
    secondaryActionText: String? = null,
    onSecondaryAction: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var deckName by remember { mutableStateOf(initialDeckName) }
    var tag by remember { mutableStateOf(initialTag) }
    val cards = remember { mutableStateListOf(*initialCards.toTypedArray()) }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            DeckEditorTopBar(title = topBarTitle, onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            DeckEditorBottomBar(
                saveText = saveText,
                secondaryActionText = secondaryActionText,
                onSecondaryAction = onSecondaryAction,
                onSave = onSave,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
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
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
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
            if (showSourceOptions) {
                item {
                    DeckSourceCard(
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White) },
                        title = "Scan from Camera",
                        subtitle = "Instantly turn notes or books into flashcards.",
                        iconBackground = Indigo500,
                    )
                }
                item {
                    DeckSourceCard(
                        icon = { Icon(Icons.Default.UploadFile, contentDescription = null, tint = Indigo500) },
                        title = "Upload Document",
                        subtitle = "Import PDFs or images from your device.",
                        iconBackground = BiologyTagBackground,
                    )
                }
                item {
                    DividerWithText("OR")
                }
            }
            item {
                Text("Manual Entry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            itemsIndexed(cards) { index, card ->
                ManualCardEditor(
                    index = index,
                    card = card,
                    canDelete = cards.size > 1,
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
                    Text("Add Empty Card", modifier = Modifier.padding(start = Spacing.xs))
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
            .background(AppBackground)
            .padding(horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Indigo500,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = { }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        .background(InputBackground, MaterialTheme.shapes.extraLarge),
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
private fun DeckSourceCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    iconBackground: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(18.dp, MaterialTheme.shapes.extraLarge)
                    .background(iconBackground, MaterialTheme.shapes.extraLarge),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = Spacing.md),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
private fun DividerWithText(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = DividerDefaults.color.copy(alpha = 0.6f))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Spacing.md),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = DividerDefaults.color.copy(alpha = 0.6f))
    }
}

@Composable
private fun ManualCardEditor(
    index: Int,
    card: DeckEditorCardInput,
    canDelete: Boolean,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
            )
            Text("Back (Definition)", style = MaterialTheme.typography.labelSmall)
            FlatTextField(
                value = card.back,
                onValueChange = onBackChange,
                placeholder = "Enter definition or answer...",
                minHeight = 96.dp,
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
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(minHeight),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        singleLine = singleLine,
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            disabledContainerColor = InputBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun DeckEditorBottomBar(
    saveText: String,
    secondaryActionText: String?,
    onSecondaryAction: () -> Unit,
    onSave: () -> Unit,
) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
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
                    modifier = Modifier.weight(1.45f),
                )
            } else {
                PrimaryButton(
                    text = saveText,
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(0.72f),
                )
            }
        }
    }
}
