package com.sologo.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Тёмная цветовая схема
val SoloDarkColorScheme = darkColorScheme(
    primary = SoloLightGreen,
    onPrimary = SoloDark,
    primaryContainer = SoloGreen,
    onPrimaryContainer = SoloWhite,

    secondary = SoloLightBlue,
    onSecondary = SoloDark,
    secondaryContainer = SoloBlue,
    onSecondaryContainer = SoloWhite,

    tertiary = SoloMint,
    onTertiary = SoloDark,
    tertiaryContainer = SoloTeal,
    onTertiaryContainer = SoloWhite,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFB0B0B0),

    error = Color(0xFFCF6679),
    onError = SoloDark,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = SoloWhite,

    outline = Color(0xFF3D3D3D),
    outlineVariant = Color(0xFF2D2D2D),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = SoloGreen,

    surfaceTint = SoloLightGreen,
)

@Composable
fun SoloGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // ← ИСПРАВЛЕНО: выбираем правильную тему
    val colorScheme = if (darkTheme) SoloDarkColorScheme else SoloLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as androidx.activity.ComponentActivity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SoloGoTypography,
        content = content
    )
}