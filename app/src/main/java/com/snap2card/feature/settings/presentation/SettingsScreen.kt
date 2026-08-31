package com.snap2card.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing

/** Settings screen. Developer C owns this. */
@Composable
fun SettingsScreen(
    onSignOut: () -> Unit,
    onAccountClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { AppTopBar(title = "Settings") }) { padding ->
        Column(Modifier.padding(padding).padding(Spacing.md).fillMaxSize()) {
            when (val state = uiState) {
                is SettingsUiState.Loading -> CircularProgressIndicator()
                is SettingsUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is SettingsUiState.Success -> {
                    val settings = state.settings
                    SettingRow(icon = { Icon(Icons.Default.AccountCircle, null) }, label = "Account", onClick = onAccountClick) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                    SettingRow(icon = { Icon(Icons.Default.DarkMode, null) }, label = "Dark Mode") {
                        Switch(checked = settings.darkMode, onCheckedChange = { viewModel.updateSettings(settings.copy(darkMode = it)) })
                    }
                    SettingRow(icon = { Icon(Icons.Default.Notifications, null) }, label = "Notifications") {
                        Switch(checked = settings.notificationsEnabled, onCheckedChange = { viewModel.updateSettings(settings.copy(notificationsEnabled = it)) })
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onSignOut, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Sign Out", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: @Composable () -> Unit,
    label: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    ListItem(
        headlineContent = { Text(label) },
        leadingContent = icon,
        trailingContent = trailing,
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
    )
    HorizontalDivider()
}
