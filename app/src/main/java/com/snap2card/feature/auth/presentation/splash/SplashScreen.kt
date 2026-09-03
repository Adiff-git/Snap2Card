package com.snap2card.feature.auth.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.feedback.AnimatedLoadingDots
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.Spacing
import com.snap2card.design_system.theme.SplashGradientEnd
import com.snap2card.design_system.theme.SplashGradientStart
import com.snap2card.design_system.theme.White

/**
 * Splash screen — gradient background, app logo, animated loading dots.
 * Checks session validity via [SplashViewModel] and navigates accordingly.
 *
 * Owner: FE1
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Login -> onNavigateToLogin()
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.StillLoading -> { /* wait */ }
        }
    }

    // ── UI ──────────────────────────────────────────────────────────────
    // Matches the Android Splash Screen API look for seamless transition
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Keep the animated loading dots to show progress if the API check is slow
            AnimatedLoadingDots()
        }
    }
}
