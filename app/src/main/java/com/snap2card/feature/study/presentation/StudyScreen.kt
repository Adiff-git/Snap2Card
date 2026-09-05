package com.snap2card.feature.study.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.cards.FlashCard
import com.snap2card.design_system.components.feedback.EmptyState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.theme.Spacing
import com.snap2card.feature.study.domain.model.ReviewResult

/** Study/Review Cards screen. Developer C owns this. */
@Composable
fun StudyScreen(
    deckTitle: String = "Study Session", // TODO: pass real deck title once DeckDetail exists
    onFinished: () -> Unit,
    onClose: () -> Unit = onFinished,
    viewModel: StudyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { StudyTopBar(deckTitle = deckTitle, onClose = onClose) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                is StudyUiState.Loading -> LoadingIndicator()
                is StudyUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is StudyUiState.Completed -> {
                    EmptyState("Session complete! \uD83C\uDF89", action = {
                        Button(
                            onClick = onFinished,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) { Text("Back to Decks", style = MaterialTheme.typography.labelLarge) }
                    })
                }
                is StudyUiState.Studying -> {
                    val card = state.cards[state.currentIndex]
                    Column(
                        Modifier.fillMaxSize().padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    ) {
                        MasteryProgressRow(
                            current = state.currentIndex + 1,
                            total = state.cards.size,
                            progress = state.masteryPercent / 100f,
                        )
                        Spacer(Modifier.height(Spacing.lg))
                        key(card.id) {
                            FlashCard(front = card.front, back = card.back, modifier = Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(Spacing.lg))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.recordAnswer(ReviewResult.AGAIN) },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary,
                                ),
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(Spacing.xs))
                                Text("Again", style = MaterialTheme.typography.labelLarge)
                            }
                            Button(
                                onClick = { viewModel.recordAnswer(ReviewResult.GOT_IT) },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(Spacing.xs))
                                Text("Got it", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom top bar matching the design mock: X close icon, centered
 * two-line title ("Study Mode" caption + deck title headline).
 * Built inline since AppTopBar has no subtitle slot — no shared
 * component was edited.
 */
@Composable
private fun StudyTopBar(deckTitle: String, onClose: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding() // ← add this
        ) {
            Box(
                Modifier.fillMaxWidth().padding(horizontal = Spacing.sm, vertical = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Study Mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = deckTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** "Mastery Progress" label + "n / total" fraction on one row, bar below. */
@Composable
private fun MasteryProgressRow(current: Int, total: Int, progress: Float) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Mastery Progress",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "$current / $total",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(Spacing.xs))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(MaterialTheme.shapes.extraLarge),
        )
    }
}
