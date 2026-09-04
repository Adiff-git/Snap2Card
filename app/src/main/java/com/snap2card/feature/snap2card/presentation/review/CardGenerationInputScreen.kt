package com.snap2card.feature.snap2card.presentation.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.Spacing

@Composable
fun CardGenerationInputScreen(
    onNavigateBack: () -> Unit,
    onCardsGenerated: (jobId: String) -> Unit,
    viewModel: CardGenerationInputViewModel = hiltViewModel(),
    deckId: String,
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is CardGenerationInputUiState.Loading -> GeneratingCardsContent(
            source = state.source,
            onNavigateBack = onNavigateBack,
        )
        is CardGenerationInputUiState.Error -> GenerationErrorContent(
            source = state.source,
            message = state.message,
            onRetry = viewModel::retry,
            onNavigateBack = onNavigateBack,
        )
        is CardGenerationInputUiState.Success -> {
            androidx.compose.runtime.LaunchedEffect(state.jobId) { onCardsGenerated(state.jobId) }
            GeneratingCardsContent(
                source = state.source,
                onNavigateBack = onNavigateBack,
                message = "Opening results...",
            )
        }
    }
}

@Composable
private fun GeneratingCardsContent(
    source: GenerationSource,
    onNavigateBack: () -> Unit,
    message: String = "Analyzing your notes and creating flashcards...",
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Generating Cards",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(color = Indigo500)
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.sm),
            )
            Text(
                text = source.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.sm),
            )
        }
    }
}

@Composable
private fun GenerationErrorContent(
    source: GenerationSource?,
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Generating Cards",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Could not generate cards",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.sm),
            )
            if (source != null) {
                Text(
                    text = source.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            }
            Spacer(Modifier.height(Spacing.md))
            PrimaryButton(text = "Try Again", onClick = onRetry)
            Spacer(Modifier.height(Spacing.sm))
            SecondaryButton(text = "Choose Another File", onClick = onNavigateBack)
        }
    }
}