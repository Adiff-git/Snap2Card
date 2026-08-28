package com.snap2card.feature.deck.presentation.create

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.inputs.AppTextField
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing

/** Create New Deck screen. Developer B owns this. */
@Composable
fun CreateDeckScreen(
    onDeckCreated: (deckId: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateDeckViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is CreateDeckUiState.Success) {
            onDeckCreated((uiState as CreateDeckUiState.Success).deckId)
        }
    }

    Scaffold(topBar = { AppTopBar(title = "Create Deck", navigationIcon = Icons.Default.ArrowBack, onNavigationClick = onNavigateBack) }) { padding ->
        Column(Modifier.padding(padding).padding(Spacing.md).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            AppTextField(value = title, onValueChange = { title = it }, label = "Deck Title")
            AppTextField(value = description, onValueChange = { description = it }, label = "Description", singleLine = false)
            Spacer(Modifier.weight(1f))
            if (uiState is CreateDeckUiState.Error) {
                Text((uiState as CreateDeckUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            PrimaryButton(
                text = if (uiState is CreateDeckUiState.Loading) "Creating…" else "Create Deck",
                onClick = { viewModel.createDeck(title, description) },
                enabled = uiState !is CreateDeckUiState.Loading,
            )
        }
    }
}
