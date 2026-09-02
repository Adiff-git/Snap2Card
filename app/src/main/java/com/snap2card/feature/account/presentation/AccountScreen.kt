@file:OptIn(ExperimentalMaterial3Api::class)

package com.snap2card.feature.account.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AccountScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Account",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is AccountUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is AccountUiState.SignedOut -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { Text("Signed out") }

            is AccountUiState.Success -> AccountContent(
                state = state,
                padding = padding,
                onBirthdayClick = { showDatePicker = true },
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (uiState as? AccountUiState.Success)?.birthday,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setBirthday(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun AccountContent(
    state: AccountUiState.Success,
    padding: PaddingValues,
    onBirthdayClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(padding).fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item { ProfileHeader(name = state.user.displayName, email = state.user.email, photoUrl = state.user.photoUrl) }

        item {
            SettingRowLike(
                icon = { Icon(Icons.Default.Cake, contentDescription = null) },
                label = "Birthday",
                value = state.birthday?.let { formatDate(it) } ?: "Not set",
                onClick = onBirthdayClick,
            )
        }

        item { StreakCard(streak = state.streak) }

        item {
            Text("Deck History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        if (state.decks.isEmpty()) {
            item { Text("No decks yet", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(state.decks, key = { it.id }) { deck ->
                ListItem(headlineContent = { Text(deck.title) }, supportingContent = { Text(deck.description) })
            }
        }

        item {
            Text("Review History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        if (state.reviews.isEmpty()) {
            item { Text("No reviews yet", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(state.reviews.take(20), key = { it.id }) { review ->
                ListItem(
                    headlineContent = { Text(review.result.name) },
                    supportingContent = { Text(formatDate(review.reviewedAt)) },
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(name: String, email: String, photoUrl: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Profile photo",
            modifier = Modifier.size(80.dp).clip(androidx.compose.foundation.shape.CircleShape),
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StreakCard(streak: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Spacing.md).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Current Streak", style = MaterialTheme.typography.bodyLarge)
            Text("$streak days", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingRowLike(icon: @Composable () -> Unit, label: String, value: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(label) },
        supportingContent = { Text(value) },
        leadingContent = icon,
        modifier = Modifier.clickable(onClick = onClick),
    )
    HorizontalDivider()
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))