package com.snap2card.design_system.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val LightColorScheme = lightColorScheme(
    primary = Indigo500,
    onPrimary = White,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo500,
    secondary = Indigo400,
    onSecondary = White,
    background = White,
    onBackground = Gray900,
    surface = Gray50,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,
    outline = Gray200,
    error = Error,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo400,
    onPrimary = White,
    primaryContainer = Indigo500,
    onPrimaryContainer = Indigo100,
    secondary = Indigo300,
    onSecondary = Gray900,
    background = SurfaceDark,
    onBackground = Gray100,
    surface = SurfaceVariantDark,
    onSurface = Gray100,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Gray400,
    outline = Gray600,
    error = Error,
    onError = White,
)

/**
 * App-wide Compose theme. All screens must be wrapped in this.
 * Injected once from MainActivity — feature screens must NOT create their own MaterialTheme.
 */
@Composable
fun Snap2CardTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSpacing provides SpacingValues()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

/** Extension for convenient access: `MaterialTheme.spacing.md`. */
val MaterialTheme.spacing: SpacingValues
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
