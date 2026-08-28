package com.snap2card.feature.settings.presentation

import com.snap2card.feature.settings.domain.model.UserSettings

sealed class SettingsUiState {
    data object Loading : SettingsUiState()
    data class Success(val settings: UserSettings) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}
