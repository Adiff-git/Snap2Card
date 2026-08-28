package com.snap2card.feature.deck.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.cards.DeckCard
import com.snap2card.design_system.components.feedback.EmptyState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing

/** My Decks screen. Developer B owns this. */
@Composable
fun DeckListScreen(
    onDeckClick: (String) -> Unit,
    onCreateDeck: () -> Unit,
    viewModel: DeckListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "My Decks") },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateDeck) {
                Icon(Icons.Default.Add, "Create deck")
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DeckListUiState.Loading -> LoadingIndicator()
                is DeckListUiState.Empty -> EmptyState("No decks yet. Create your first one!", action = {
                    Button(onClick = onCreateDeck) { Text("Create Deck") }
                })
                is DeckListUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is DeckListUiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(state.decks, key = { it.id }) { deck ->
                        DeckCard(title = deck.title, cardCount = deck.cardCount, onClick = { onDeckClick(deck.id) })
                    }
                }
            }
        }
    }
}
