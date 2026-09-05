package com.snap2card.feature.history.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.feedback.EmptyState
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing
import com.snap2card.feature.history.domain.model.DayCount
import com.snap2card.feature.history.domain.model.HistorySession

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { AppTopBar(title = "History") }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> LoadingIndicator()
                is HistoryUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
                is HistoryUiState.Empty -> EmptyState("No study sessions yet. Start studying!")
                is HistoryUiState.Loaded -> HistoryContent(state)
            }
        }
    }
}

@Composable
private fun HistoryContent(state: HistoryUiState.Loaded) {
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        item { Spacer(Modifier.height(Spacing.sm)) }
        item { StudyActivityCard(state.streakDays, state.cardsThisMonth, state.dailyCounts) }
        item {
            Text("Past Sessions", style = MaterialTheme.typography.titleLarge)
        }
        state.sessionsByDay.forEach { section ->
            item {
                Text(
                    section.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(section.sessions) { session -> SessionRow(session) }
        }
        item { Spacer(Modifier.height(Spacing.lg)) }
    }
}

@Composable
private fun StudyActivityCard(streakDays: Int, cardsThisMonth: Int, dailyCounts: List<DayCount>) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Spacing.md),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Current Streak", style = MaterialTheme.typography.labelMedium)
                Text(
                    "$streakDays days",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Cards this Month", style = MaterialTheme.typography.labelMedium)
                Text("$cardsThisMonth", style = MaterialTheme.typography.headlineSmall)
            }
        }
        Spacer(Modifier.height(Spacing.md))
        ActivityHeatmap(dailyCounts)
    }
}

/** Simple GitHub-style contribution grid. No shared design-system component exists for
 * this yet — if one gets added later, swap this out rather than duplicating. */
@Composable
private fun ActivityHeatmap(dailyCounts: List<DayCount>) {
    val maxCount = (dailyCounts.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(120.dp), // ~5 weeks at 20dp/cell — adjust to taste
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(dailyCounts) { day ->
            val intensity = (day.count.toFloat() / maxCount).coerceIn(0f, 1f)
            Box(
                Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f + intensity * 0.85f)
                    )
            )
        }
    }
    Spacer(Modifier.height(Spacing.xs))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
        Text("Less", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.width(Spacing.xs))
        listOf(0.15f, 0.4f, 0.65f, 1f).forEach { a ->
            Box(
                Modifier.size(10.dp).clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = a))
            )
            Spacer(Modifier.width(2.dp))
        }
        Text("More", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SessionRow(session: HistorySession) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // TODO: category-specific icon/color — mockup shows a themed circle per deck (🧪, 文A).
        // No such mapping exists in HistorySession yet; punting to a generic icon for now.
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer)
        )
        Column(Modifier.weight(1f)) {
            Text(session.title, style = MaterialTheme.typography.titleMedium)
            Text(
                "${session.cardsReviewed} Cards Reviewed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        // TODO: format session.completedAt as "10:30 AM" and show the status chip under it
    }
}