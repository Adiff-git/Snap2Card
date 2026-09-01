package com.snap2card.design_system.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale used throughout the app.
 * Use these instead of hardcoded dp values in composables.
 *
 * Access via `MaterialTheme.spacing` extension (see Theme.kt)
 * or directly as `Spacing.md`, etc.
 */
data class SpacingValues(
    val xxs: Dp = 2.dp,
    val xs: Dp  = 4.dp,
    val sm: Dp  = 8.dp,
    val md: Dp  = 16.dp,
    val lg: Dp  = 24.dp,
    val xl: Dp  = 32.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 64.dp,
)

/** Convenience singleton for direct usage (Spacing.md). */
object Spacing {
    val xxs: Dp = 2.dp
    val xs: Dp  = 4.dp
    val sm: Dp  = 8.dp
    val md: Dp  = 16.dp
    val lg: Dp  = 24.dp
    val xl: Dp  = 32.dp
    val xxl: Dp = 48.dp
    val xxxl: Dp = 64.dp
}

/** CompositionLocal for spacing values, provided via [Snap2CardTheme]. */
val LocalSpacing = staticCompositionLocalOf { SpacingValues() }
