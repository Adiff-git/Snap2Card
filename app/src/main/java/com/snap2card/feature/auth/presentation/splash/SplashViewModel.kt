package com.snap2card.feature.auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snap2card.feature.auth.domain.usecase.CheckSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Navigation target produced by the session check.
 */
sealed class SplashDestination {
    data object StillLoading : SplashDestination()
    data object Login : SplashDestination()
    data object Home : SplashDestination()
}

/**
 * Performs a session check on init and emits the appropriate navigation target.
 * The splash screen observes [destination] and navigates once a value arrives.
 * A minimum display time of 1.5 s ensures the branding is visible.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.StillLoading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            // Ensure branding is visible for at least 1.5 s
            delay(1_500L)
            val hasSession = try {
                checkSessionUseCase()
            } catch (_: Exception) {
                false
            }
            _destination.value =
                if (hasSession) SplashDestination.Home else SplashDestination.Login
        }
    }
}
