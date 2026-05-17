// presentation/theme/TopAppBarColors.kt
package com.sologo.app.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun soloGoTopAppBarColors(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = Color.White,
        scrolledContainerColor = SoloOffWhite,
        navigationIconContentColor = SoloGreen,
        titleContentColor = SoloDark,
        actionIconContentColor = SoloGreen,
    )
}