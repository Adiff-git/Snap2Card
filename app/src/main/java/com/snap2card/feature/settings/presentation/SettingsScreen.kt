package com.snap2card.feature.settings.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing
import com.snap2card.feature.settings.domain.model.UserSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onSignOut: () -> Unit,
    onAccountClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    var showGoalDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar(title = "Settings") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (val state = uiState) {
                is SettingsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is SettingsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is SettingsUiState.Success -> {
                    val settings = state.settings

                    SettingsSection("Account") {
                        SettingRow(
                            icon = { Icon(Icons.Default.AccountCircle, null, tint = MaterialTheme.colorScheme.primary) },
                            label = "Profile Details",
                            onClick = onAccountClick
                        ) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    SettingsSection("Study") {
                        SettingRow(
                            icon = { Icon(Icons.Default.Flag, null, tint = MaterialTheme.colorScheme.primary) },
                            label = "Daily Study Goal",
                            subLabel = "${settings.dailyGoalCards} cards/day",
                            onClick = { showGoalDialog = true }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    SettingsSection("Appearance") {
                        SettingRow(
                            icon = { Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.primary) },
                            label = "Dark Mode"
                        ) {
                            Switch(
                                checked = settings.darkMode,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(darkMode = it)) }
                            )
                        }
                    }

                    SettingsSection("General") {
                        SettingRow(
                            icon = { Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary) },
                            label = "Notifications",
                            subLabel = "Daily reminders"
                        ) {
                            Switch(
                                checked = settings.notificationsEnabled,
                                onCheckedChange = {
                                    viewModel.updateSettings(settings.copy(notificationsEnabled = it))
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(if (it) "Notifications enabled" else "Notifications disabled")
                                    }
                                }
                            )
                        }
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = Spacing.md))
                        SettingRow(
                            icon = { Icon(Icons.Default.Language, null, tint = MaterialTheme.colorScheme.primary) },
                            label = "Language",
                            subLabel = "English"
                        ) {}
                    }

                    SettingsSection("Support") {
                        SettingRow(
                            icon = { Icon(Icons.Default.HelpOutline, null, tint = MaterialTheme.colorScheme.primary) },
                            label = "Help & Support"
                        ) {}
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = Spacing.md))
                        SettingRow(
                            icon = { Icon(Icons.Default.Feedback, null, tint = MaterialTheme.colorScheme.primary) },
                            label = "Send Feedback",
                            onClick = { showFeedbackDialog = true }
                        ) {}
                    }

                    Spacer(Modifier.height(Spacing.xl))

                    OutlinedButton(
    onClick = {
        viewModel.signOut()
        onSignOut()
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Spacing.md),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
) {
    Text("Log Out", fontWeight = FontWeight.Bold)
}
                    
                    Spacer(Modifier.height(Spacing.xxxl))
                    
                    if (showGoalDialog) {
                        DailyGoalDialog(
                            currentGoal = settings.dailyGoalCards,
                            onDismiss = { showGoalDialog = false },
                            onSave = { newGoal ->
                                viewModel.updateSettings(settings.copy(dailyGoalCards = newGoal))
                                showGoalDialog = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Daily goal updated")
                                }
                            }
                        )
                    }
                    
                    if (showFeedbackDialog) {
                        FeedbackDialog(
                            onDismiss = { showFeedbackDialog = false },
                            onSubmit = { _, _ ->
                                showFeedbackDialog = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Thank you for your feedback!")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = Spacing.sm, top = Spacing.sm, bottom = Spacing.xs)
        )
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: @Composable () -> Unit,
    label: String,
    subLabel: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(label, fontWeight = FontWeight.Medium) },
        supportingContent = subLabel?.let { { Text(it) } },
        leadingContent = icon,
        trailingContent = trailing,
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
    )
}

@Composable
private fun DailyGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Study Goal") },
        text = {
            Column {
                Text("How many cards do you want to review each day?", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(Spacing.md))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { 
                        goalText = it
                        isError = it.toIntOrNull() == null || it.toIntOrNull()!! <= 0
                    },
                    label = { Text("Cards per day") },
                    isError = isError,
                    singleLine = true,
                    supportingText = { if (isError) Text("Enter a valid number greater than 0") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    goalText.toIntOrNull()?.let {
                        if (it > 0) onSave(it)
                    }
                },
                enabled = !isError
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Send Feedback", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = "Star $i",
                                tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholder = { Text("Tell us what you think...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Button(
                        onClick = { onSubmit(feedbackText, rating) },
                        enabled = rating > 0 && feedbackText.isNotBlank()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
