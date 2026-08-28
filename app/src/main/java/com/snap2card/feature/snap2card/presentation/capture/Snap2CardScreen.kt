package com.snap2card.feature.snap2card.presentation.capture

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing

/** Snap2Card capture screen — camera / upload entry point. Developer C owns this. */
@Composable
fun Snap2CardScreen(
    onCardsGenerated: (jobId: String) -> Unit,
    viewModel: Snap2CardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is Snap2CardUiState.Success) {
            // Use a temporary jobId; in production, backend returns a real ID
            onCardsGenerated("local")
        }
    }

    Scaffold(topBar = { AppTopBar(title = "Snap2Card") }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (uiState) {
                is Snap2CardUiState.Idle -> IdleContent(
                    onCameraClick = { /* TODO: launch camera intent, then viewModel.onImageSelected(uri, mime) */ },
                    onUploadClick = { /* TODO: launch file picker */ },
                )
                is Snap2CardUiState.Uploading -> LoadingIndicator(message = "Uploading…")
                is Snap2CardUiState.Processing -> LoadingIndicator(message = "AI is generating cards…")
                is Snap2CardUiState.Success -> LoadingIndicator(message = "Done! Opening results…")
                is Snap2CardUiState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((uiState as Snap2CardUiState.Error).message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(Spacing.md))
                    PrimaryButton("Try Again", onClick = viewModel::reset)
                }
            }
        }
    }
}

@Composable
private fun IdleContent(onCameraClick: () -> Unit, onUploadClick: () -> Unit) {
    Column(Modifier.padding(Spacing.lg).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text("Create cards from an image or document", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Spacing.md))
        PrimaryButton("📷  Scan from Camera", onClick = onCameraClick)
        SecondaryButton("📄  Upload Document", onClick = onUploadClick)
    }
}
