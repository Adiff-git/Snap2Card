package com.snap2card.feature.study.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.cards.FlashCard
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.feedback.EmptyState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing
import com.snap2card.feature.study.domain.model.ReviewResult

/** Study/Review Cards screen. Developer C owns this. */
@Composable
fun StudyScreen(
    onFinished: () -> Unit,
    viewModel: StudyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { AppTopBar(title = "Study") }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                is StudyUiState.Loading -> LoadingIndicator()
                is StudyUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is StudyUiState.Completed -> {
                    EmptyState("Session complete! 🎉", action = {
                        PrimaryButton("Back to Decks", onClick = onFinished)
                    })
                }
                is StudyUiState.Studying -> {
                    val card = state.cards[state.currentIndex]
                    Column(
                        Modifier.fillMaxSize().padding(Spacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LinearProgressIndicator(
                            progress = { state.masteryPercent / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(Spacing.sm))
                        Text("${state.currentIndex + 1} / ${state.cards.size}", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(Spacing.lg))
                        FlashCard(front = card.front, back = card.back, modifier = Modifier.weight(1f))
                        Spacer(Modifier.height(Spacing.lg))
                        if (!state.isRevealed) {
                            PrimaryButton("Tap to Reveal", onClick = viewModel::revealCard)
                        } else {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                                SecondaryButton("Again", onClick = { viewModel.recordAnswer(ReviewResult.AGAIN) }, modifier = Modifier.weight(1f))
                                PrimaryButton("Got it", onClick = { viewModel.recordAnswer(ReviewResult.GOT_IT) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
