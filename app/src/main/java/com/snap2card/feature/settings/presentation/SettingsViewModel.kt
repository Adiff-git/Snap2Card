package com.snap2card.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.settings.domain.model.UserSettings
import com.snap2card.feature.settings.domain.usecase.GetSettingsUseCase
import com.snap2card.feature.settings.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = getSettingsUseCase()
        .map { SettingsUiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState.Loading)

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch { updateSettingsUseCase(settings) }
    }
}
