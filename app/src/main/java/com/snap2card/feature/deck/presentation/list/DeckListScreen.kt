package com.snap2card.feature.deck.presentation.list

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.BiologyTagBackground
import com.snap2card.design_system.theme.BiologyTagText
import com.snap2card.design_system.theme.HistoryTagBackground
import com.snap2card.design_system.theme.HistoryTagText
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.InputBackground
import com.snap2card.design_system.theme.LanguageTagBackground
import com.snap2card.design_system.theme.LanguageTagText
import com.snap2card.design_system.theme.ProgressTrack
import com.snap2card.design_system.theme.Spacing

/** My Decks screen. Developer B owns this. */
@Composable
fun DeckListScreen(
    onDeckClick: (String) -> Unit,
    onCreateDeck: () -> Unit,
) {
    val sampleDecks = remember {
        listOf(
            DeckListItemUi("1", "Biology", "Cellular Respiration", 45, 0.75f),
            DeckListItemUi("2", "Language", "Spanish Verbs - Present", 120, 0.30f),
            DeckListItemUi("3", "History", "European Capitals", 50, 0.95f),
        )
    }
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Biology", "Language", "History")
    val visibleDecks = sampleDecks.filter { deck ->
        val matchesCategory = selectedCategory == "All" || deck.category == selectedCategory
        val matchesQuery = query.isBlank() || deck.title.contains(query, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Scaffold(containerColor = AppBackground) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(AppBackground),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            item {
                DeckListHeader(onCreateDeck = onCreateDeck)
            }
            item {
                DeckSearchField(value = query, onValueChange = { query = it })
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    categories.forEach { category ->
                        item {
                            DeckFilterChip(
                                text = category,
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                            )
                        }
                    }
                }
            }
            items(visibleDecks, key = { it.id }) { deck ->
                DeckProgressCard(deck = deck, onClick = { onDeckClick(deck.id) })
            }
            if (visibleDecks.isEmpty()) {
                item {
                    Text(
                        text = "No decks found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xl),
                    )
                }
            }
        }
    }
}

private data class DeckListItemUi(
    val id: String,
    val category: String,
    val title: String,
    val cardCount: Int,
    val mastery: Float,
)

@Composable
private fun DeckListHeader(onCreateDeck: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Decks",
            style = MaterialTheme.typography.headlineMedium,
            color = Indigo500,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onCreateDeck) {
            Icon(Icons.Default.Add, contentDescription = "Create deck")
        }
    }
}

@Composable
private fun DeckSearchField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text("Search your decks...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
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
private fun DeckFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) Indigo500 else InputBackground,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
        )
    }
}

@Composable
private fun DeckProgressCard(deck: DeckListItemUi, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            DeckTag(deck.category)
            Text(
                text = deck.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${deck.cardCount} Cards",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Spacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Mastery",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${(deck.mastery * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(ProgressTrack),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(deck.mastery)
                        .height(7.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(Indigo500),
                )
            }
        }
    }
}

@Composable
private fun DeckTag(category: String) {
    val colors = when (category) {
        "Biology" -> BiologyTagBackground to BiologyTagText
        "Language" -> LanguageTagBackground to LanguageTagText
        else -> HistoryTagBackground to HistoryTagText
    }
    Surface(shape = MaterialTheme.shapes.extraLarge, color = colors.first) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = colors.second,
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        )
    }
}
