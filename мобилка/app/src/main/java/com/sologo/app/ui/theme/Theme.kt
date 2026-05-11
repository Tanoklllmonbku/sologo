package com.sologo.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = MintContainer,
    onPrimaryContainer = OnMintContainer,
    secondary = SkySecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF075985),
    tertiary = CoralAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4E1),
    onTertiaryContainer = Color(0xFF7F1D1D),
    background = PageBackground,
    onBackground = SlateInk,
    surface = SurfaceLight,
    onSurface = SlateInk,
    surfaceVariant = PageBackgroundAlt,
    onSurfaceVariant = SlateMuted,
    outline = OutlineSoft,
    surfaceContainerLow = Color(0xFFF8FBFB),
    surfaceContainer = Color(0xFFF1F6F5),
    surfaceContainerHigh = Color(0xFFE8EEED),
    surfaceContainerHighest = Color(0xFFDDE5E3),
)

private val DarkColors = darkColorScheme(
    primary = TealBright,
    onPrimary = DarkBackground,
    primaryContainer = TealDim,
    onPrimaryContainer = MintContainer,
    secondary = Color(0xFF7DD3FC),
    onSecondary = DarkBackground,
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = Color(0xFFFFA69A),
    onTertiary = DarkBackground,
    tertiaryContainer = Color(0xFF7C2D12),
    onTertiaryContainer = Color(0xFFFFE4E1),
    background = DarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    surfaceContainerLow = Color(0xFF121A2A),
    surfaceContainer = Color(0xFF151D2E),
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = Color(0xFF243044),
)

@Composable
fun SoloGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = scheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        shapes = SoloGoShapes,
        content = content,
    )
}
