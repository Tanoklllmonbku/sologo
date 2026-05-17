// presentation/theme/Color.kt
package com.sologo.app.presentation.theme

import androidx.compose.ui.graphics.Color

// Основные цвета
val SoloGreen = Color(0xFF2E7D32)      // Тёмно-зелёный (акценты)
val SoloLightGreen = Color(0xFF81C784) // Светло-зелёный
val SoloMint = Color(0xFFA5D6A7)       // Мятный
val SoloTeal = Color(0xFF00897B)       // Бирюзовый

val SoloBlue = Color(0xFF1976D2)       // Синий (акценты)
val SoloLightBlue = Color(0xFF64B5F6)  // Светло-синий
val SoloSky = Color(0xFFB3E5FC)        // Небесный

// Нейтральные цвета
val SoloWhite = Color(0xFFFFFFFF)
val SoloOffWhite = Color(0xFFF5F5F5)
val SoloGray100 = Color(0xFFF0F0F0)
val SoloGray300 = Color(0xFFE0E0E0)
val SoloGray500 = Color(0xFF9E9E9E)
val SoloGray700 = Color(0xFF616161)
val SoloDark = Color(0xFF212121)

// Семантические цвета
val SoloSuccess = SoloGreen
val SoloError = Color(0xFFD32F2F)
val SoloWarning = Color(0xFFFFA000)
val SoloInfo = SoloBlue

// Material Design 3 цветовая схема - Светлая тема
val SoloLightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = SoloGreen,
    onPrimary = SoloWhite,
    primaryContainer = SoloLightGreen,
    onPrimaryContainer = SoloDark,

    secondary = SoloBlue,
    onSecondary = SoloWhite,
    secondaryContainer = SoloLightBlue,
    onSecondaryContainer = SoloDark,

    tertiary = SoloTeal,
    onTertiary = SoloWhite,
    tertiaryContainer = SoloMint,
    onTertiaryContainer = SoloDark,

    background = SoloOffWhite,
    onBackground = SoloDark,

    surface = SoloWhite,
    onSurface = SoloDark,
    surfaceVariant = SoloGray100,
    onSurfaceVariant = SoloGray700,

    error = SoloError,
    onError = SoloWhite,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = SoloDark,

    outline = SoloGray300,
    outlineVariant = SoloGray100,

    scrim = Color(0xFF000000),
    inverseSurface = SoloDark,
    inverseOnSurface = SoloWhite,
    inversePrimary = SoloLightGreen,

    surfaceTint = SoloGreen,
)