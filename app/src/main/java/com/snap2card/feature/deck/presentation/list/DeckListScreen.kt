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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.InputBackground
import com.snap2card.design_system.theme.Spacing
import com.snap2card.feature.deck.domain.model.Deck

/** My Decks screen. Developer B owns this. */
@Composable
fun DeckListScreen(
    onDeckClick: (String) -> Unit,
    onCreateDeck: () -> Unit,
    viewModel: DeckListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    val visibleDecks = (uiState as? DeckListUiState.Success)
        ?.decks
        ?.filter { deck -> query.isBlank() || deck.title.contains(query, ignoreCase = true) }
        .orEmpty()

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

            when (val state = uiState) {
                DeckListUiState.Loading -> item { LoadingState() }
                DeckListUiState.Empty -> item { EmptyState("No decks yet.") }
                is DeckListUiState.Error -> item { ErrorState(state.message) }
                is DeckListUiState.Success -> {
                    if (visibleDecks.isEmpty()) {
                        item { EmptyState("No decks found.") }
                    } else {
                        items(visibleDecks, key = { it.id }) { deck ->
                            DeckCategoryCard(deck = deck, onClick = { onDeckClick(deck.id) })
                        }
                    }
                }
            }
        }
    }
}

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
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Indigo500)
    }
}

@Composable
private fun EmptyState(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = Spacing.xl),
    )
}

@Composable
private fun ErrorState(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = Spacing.xl),
    )
}

@Composable
private fun DeckCategoryCard(deck: Deck, onClick: () -> Unit) {
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
            Text(
                text = deck.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (deck.cardCount > 0) "${deck.cardCount} Cards" else "Card count unavailable",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (deck.description.isNotBlank()) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = deck.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
