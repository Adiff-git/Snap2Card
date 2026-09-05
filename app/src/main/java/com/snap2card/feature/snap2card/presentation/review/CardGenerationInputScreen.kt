package com.snap2card.feature.snap2card.presentation.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
            message = state.message,
        )
        is CardGenerationInputUiState.OcrPreview -> OcrPreviewContent(
            state = state,
            onRawTextChange = viewModel::updateRawText,
            onGenerateCards = viewModel::generateCardsFromPreview,
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
private fun OcrPreviewContent(
    state: CardGenerationInputUiState.OcrPreview,
    onRawTextChange: (String) -> Unit,
    onGenerateCards: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val isPdf = state.source.mimeType == "application/pdf"
    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isPdf) "Review PDF Text" else "Review Scanned Text",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = Spacing.sm) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    if (state.generationError != null) {
                        Text(
                            text = state.generationError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    PrimaryButton(
                        text = "Generate Cards",
                        onClick = onGenerateCards,
                        enabled = state.rawText.isNotBlank(),
                    )
                    SecondaryButton(text = if (isPdf) "Choose Another File" else "Scan Again", onClick = onNavigateBack)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = if (isPdf) "Check the extracted PDF text before generating cards." else "Check the OCR text before generating cards.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Edit anything that was extracted incorrectly, then generate cards from this text.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${state.characterCount} characters • ${state.source.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = state.rawText,
                onValueChange = onRawTextChange,
                label = { Text(if (isPdf) "Extracted text" else "Scanned text") },
                minLines = 12,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Spacing.xxl * 5),
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
