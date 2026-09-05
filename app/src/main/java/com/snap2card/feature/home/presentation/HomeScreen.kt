package com.snap2card.feature.home.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.chips.CategoryChip
import com.snap2card.design_system.components.chips.tagColors
import com.snap2card.design_system.components.feedback.ErrorState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.theme.*
import com.snap2card.feature.home.domain.model.RecentDeck
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Home / Dashboard screen — matches design 1.pdf.
 * Sections: Greeting + streak, Capture card, Recent Decks, Daily Goal.
 *
 * Owner: FE1
 */
@Composable
fun HomeScreen(
    onDeckClick: (String) -> Unit,
    onSnapClick: () -> Unit,
    onReviewClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val deleteMessage = (uiState as? HomeUiState.Success)?.deleteMessage
    val deleteError = (uiState as? HomeUiState.Success)?.deleteError

    LaunchedEffect(deleteMessage, deleteError) {
        val message = deleteMessage ?: deleteError
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearDeleteNotice()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingIndicator()
                is HomeUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadDashboard() },
                )
                is HomeUiState.Success -> HomeContent(
                    state = state,
                    onDeckClick = onDeckClick,
                    onSnapClick = onSnapClick,
                    onReviewClick = onReviewClick,
                    onDeleteDeck = viewModel::deleteDeck,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onDeckClick: (String) -> Unit,
    onSnapClick: () -> Unit,
    onReviewClick: () -> Unit,
    onDeleteDeck: (RecentDeck) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.xxl),
    ) {
        // ── Greeting Row ───────────────────────────────────────────────
        GreetingSection(
            userName = state.userName,
            streakCount = state.streakCount,
        )

        Spacer(Modifier.height(Spacing.lg))

        // ── Capture New Deck Card ──────────────────────────────────────
        CaptureCard(onClick = onSnapClick)

        Spacer(Modifier.height(Spacing.lg))

        // ── Recent Decks ───────────────────────────────────────────────
        RecentDecksSection(
            decks = state.recentDecks,
            deletingDeckIds = state.deletingDeckIds,
            onDeckClick = onDeckClick,
            onDeleteDeck = onDeleteDeck,
        )

        Spacer(Modifier.height(Spacing.lg))

        // ── Daily Goal ─────────────────────────────────────────────────
        DailyGoalCard(
            total = state.dailyGoalTotal,
            completed = state.dailyGoalCompleted,
            onReviewClick = onReviewClick,
        )

        Spacer(Modifier.height(Spacing.lg))
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Greeting
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun GreetingSection(
    userName: String,
    streakCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Indigo100),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "User avatar",
                tint = Indigo500,
                modifier = Modifier.size(28.dp),
            )
        }

        Spacer(Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hi, $userName!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        // Streak badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(TagOrangeBg)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = "Streak",
                tint = TagOrange,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(Spacing.xs))
            Text(
                text = "$streakCount",
                style = MaterialTheme.typography.labelLarge,
                color = TagOrange,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Capture Card (Indigo gradient hero)
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun CaptureCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(CaptureGradientStart, CaptureGradientEnd)
                    )
                )
                .padding(Spacing.lg),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Capture New Deck",
                        style = MaterialTheme.typography.titleLarge,
                        color = White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = "Snap a photo of your notes to create flashcards instantly",
                        style = MaterialTheme.typography.bodySmall,
                        color = White.copy(alpha = 0.85f),
                    )
                }
                Spacer(Modifier.width(Spacing.md))
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Camera",
                        tint = White,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Recent Decks
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun RecentDecksSection(
    decks: List<RecentDeck>,
    deletingDeckIds: Set<String>,
    onDeckClick: (String) -> Unit,
    onDeleteDeck: (RecentDeck) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Recent Decks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        TextButton(onClick = { /* TODO: Navigate to full deck list */ }) {
            Text(
                "View All",
                style = MaterialTheme.typography.labelLarge,
                color = Indigo500,
            )
        }
    }

    Spacer(Modifier.height(Spacing.sm))

    LazyRow(
        contentPadding = PaddingValues(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(decks, key = { it.id }) { deck ->
            RecentDeckCard(
                deck = deck,
                isDeleting = deck.id in deletingDeckIds,
                onClick = { onDeckClick(deck.id) },
                onDelete = { onDeleteDeck(deck) },
            )
        }
    }
}

@Composable
private fun RecentDeckCard(
    deck: RecentDeck,
    isDeleting: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val (tagColor, tagBg) = tagColors(deck.category)
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDeckDialog(
            deckTitle = deck.title,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
        )
    }

    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(enabled = !isDeleting, onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryChip(
                    label = deck.category,
                    textColor = tagColor,
                    backgroundColor = tagBg,
                )
                IconButton(
                    onClick = { menuExpanded = true },
                    enabled = !isDeleting,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More options",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            menuExpanded = false
                            showDeleteDialog = true
                        },
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = deck.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = "${deck.cardCount} ${if (deck.cardCount == 1) "Card" else "Cards"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(Spacing.sm))

            // Mastery progress bar
            LinearProgressIndicator(
                progress = { deck.masteryPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Indigo500,
                trackColor = Indigo100,
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = "${(deck.masteryPercent * 100).toInt()}% mastery",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ConfirmDeleteDeckDialog(
    deckTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete deck?") },
        text = { Text("Are you sure you want to delete \"$deckTitle\"? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

// ═══════════════════════════════════════════════════════════════════════════
// Daily Goal
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun DailyGoalCard(
    total: Int,
    completed: Int,
    onReviewClick: () -> Unit,
) {
    val safeTotal = total.coerceAtLeast(1)
    val safeCompleted = completed.coerceIn(0, safeTotal)
    val remaining = safeTotal - safeCompleted
    val progress = safeCompleted.toFloat() / safeTotal.toFloat()
    val remainingText = if (remaining == 0) {
        "Goal completed today"
    } else {
        "$remaining left today"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Lightning icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(TagOrangeBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = "Daily Goal",
                    tint = TagOrange,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Goal: $safeTotal Cards",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    text = remainingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(Spacing.sm))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Indigo500,
                    trackColor = Indigo100,
                )
            }

            TextButton(onClick = onReviewClick) {
                Text(
                    "Review",
                    style = MaterialTheme.typography.labelLarge,
                    color = Indigo500,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
