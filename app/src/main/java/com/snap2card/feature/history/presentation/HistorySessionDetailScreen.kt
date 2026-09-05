package com.snap2card.feature.history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing
import com.snap2card.feature.study.domain.model.ExamReviewDetail
import com.snap2card.feature.study.domain.model.QuizResult

@Composable
fun HistorySessionDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Exam Review",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is HistoryDetailUiState.Loading -> LoadingIndicator()
                is HistoryDetailUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
                is HistoryDetailUiState.Loaded -> HistoryDetailContent(state.detail)
            }
        }
    }
}

@Composable
private fun HistoryDetailContent(detail: ExamReviewDetail) {
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            Column(Modifier.padding(vertical = Spacing.md)) {
                Text(detail.examName, style = MaterialTheme.typography.titleLarge)
                Text("${detail.resultScore}/${detail.totalScore} correct", style = MaterialTheme.typography.bodyLarge)
            }
        }
        items(detail.quizResults) { quiz -> QuizResultRow(quiz) }
    }
}

@Composable
private fun QuizResultRow(quiz: QuizResult) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (quiz.isCorrect) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            )
            .padding(Spacing.md),
    ) {
        Column(Modifier.weight(1f)) {
            Text(quiz.frontSide, style = MaterialTheme.typography.titleMedium)
            Text(quiz.backSide, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            if (quiz.isCorrect) Icons.Default.Check else Icons.Default.Close,
            contentDescription = if (quiz.isCorrect) "Correct" else "Incorrect",
        )
    }
}