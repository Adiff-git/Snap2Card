package com.snap2card.feature.history.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.snap2card.design_system.components.feedback.EmptyState
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing

/** History screen — study activity log. Developer C owns this. */
@Composable
fun HistoryScreen() {
    Scaffold(topBar = { AppTopBar(title = "History") }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            // TODO: implement history list with HistoryViewModel + GetStudyHistoryUseCase
            EmptyState("No study sessions yet. Start studying!")
        }
    }
}
