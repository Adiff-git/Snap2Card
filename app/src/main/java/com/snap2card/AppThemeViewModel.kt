package com.snap2card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.settings.domain.usecase.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
) : ViewModel() {
    val darkTheme: StateFlow<Boolean> = getSettingsUseCase()
        .map { it.darkMode }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
}