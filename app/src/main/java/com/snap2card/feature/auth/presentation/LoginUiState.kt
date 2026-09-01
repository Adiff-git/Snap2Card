package com.snap2card.feature.auth.presentation

import com.snap2card.feature.auth.domain.model.User

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    /** Email/password login succeeded (token saved, no full user data). */
    data object LoggedIn : LoginUiState()
    /** Google OAuth login succeeded (full user data available). */
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
