package com.snap2card.feature.home.presentation

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

/** Home / Dashboard screen. Developer A owns this. */
@Composable
fun HomeScreen(
    onDeckClick: (String) -> Unit,
    onSnapClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AppTopBar(title = "Snap2Card") },
        floatingActionButton = {
            FloatingActionButton(onClick = onSnapClick) {
                Icon(Icons.Default.Add, contentDescription = "Snap new deck")
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingIndicator()
                is HomeUiState.Error -> Text(state.message)
                is HomeUiState.Success -> {
                    LazyColumn(contentPadding = PaddingValues(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        item {
                            Text(state.greeting, style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(Spacing.md))
                            Text("Recent Decks", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(Spacing.sm))
                        }
                        if (state.recentDecks.isEmpty()) {
                            item { EmptyState("No decks yet. Tap + to create one!") }
                        } else {
                            items(state.recentDecks, key = { it.id }) { deck ->
                                DeckCard(title = deck.title, cardCount = deck.cardCount, onClick = { onDeckClick(deck.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
