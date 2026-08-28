package com.snap2card.feature.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.theme.Spacing

/**
 * Splash screen — determines whether to route to Login or Home.
 * Developer A owns this screen.
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    // TODO: Check session validity and route accordingly
    LaunchedEffect(Unit) {
        onNavigateToLogin()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Snap2Card",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/**
 * Login screen — Google Sign-In entry point.
 * Developer A owns this screen.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) onLoginSuccess()
    }

    Box(Modifier.fillMaxSize().padding(Spacing.lg), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome to Snap2Card", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(Spacing.sm))
            Text("Turn your notes into flashcards instantly.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(Spacing.xxl))

            if (uiState is LoginUiState.Loading) {
                LoadingIndicator()
            } else {
                PrimaryButton(
                    text = "Sign in with Google",
                    onClick = {
                        // TODO: Launch Google Credential Manager flow, then call viewModel.signInWithGoogle(idToken)
                    }
                )
            }

            if (uiState is LoginUiState.Error) {
                Spacer(Modifier.height(Spacing.md))
                Text((uiState as LoginUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
