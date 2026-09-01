package com.snap2card.feature.deck.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.BiologyTagBackground
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.InputBackground
import com.snap2card.design_system.theme.MedicalTagBackground
import com.snap2card.design_system.theme.MedicalTagText
import com.snap2card.design_system.theme.Spacing

/** Create New Deck screen. Developer B owns this. */
@Composable
fun CreateDeckScreen(
    onDeckCreated: (deckId: String) -> Unit,
    onCardCreationMethodSelected: (method: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    var deckName by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("Medical") }
    val previewDeckId = deckName.ifBlank { "preview-deck" }.trim().lowercase().replace(" ", "-")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CreateDeckTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                PrimaryButton(
                    text = "Create Deck",
                    onClick = { onDeckCreated(previewDeckId) },
                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            DeckSetupCard(
                deckName = deckName,
                onDeckNameChange = { deckName = it },
                tag = tag,
                onAddTag = { tag = if (tag.isBlank()) "Medical" else tag },
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = "ADD CARDS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )

            CardCreationMethodCard(
                icon = Icons.Default.CameraAlt,
                title = "Scan with Camera",
                subtitle = "Capture notes or textbook pages and generate cards.",
                iconBackground = Indigo500,
                iconTint = Color.White,
                onClick = { onCardCreationMethodSelected("camera") },
            )
            CardCreationMethodCard(
                icon = Icons.Default.UploadFile,
                title = "Import Document",
                subtitle = "Upload PDFs or images from your device.",
                iconBackground = BiologyTagBackground,
                iconTint = Indigo500,
                onClick = { onCardCreationMethodSelected("document") },
            )
            CardCreationMethodCard(
                icon = Icons.Default.EditNote,
                title = "Add Cards Manually",
                subtitle = "Type your own terms and definitions before review.",
                iconBackground = MedicalTagBackground,
                iconTint = Indigo500,
                onClick = { onCardCreationMethodSelected("manual") },
            )
        }
    }
}

@Composable
private fun CreateDeckTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(AppBackground)
            .padding(horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = "Create Deck",
            style = MaterialTheme.typography.titleLarge,
            color = Indigo500,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DeckSetupCard(
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
            )
            Text("Tags", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onAddTag,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .height(30.dp)
                        .background(InputBackground, MaterialTheme.shapes.extraLarge),
                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = 0.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text("Add Tag", style = MaterialTheme.typography.labelSmall)
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
private fun CardCreationMethodCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(16.dp, MaterialTheme.shapes.extraLarge)
                    .background(iconBackground, MaterialTheme.shapes.extraLarge),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FlatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        singleLine = true,
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
