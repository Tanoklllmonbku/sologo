package com.sologo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import com.sologo.app.presentation.navigation.SoloGoNavHost
import com.sologo.app.presentation.theme.SoloGoTheme
import com.sologo.app.utils.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoloGoApp()
        }
    }
}

@Composable
fun SoloGoApp() {
    val themeManager: ThemeManager = getKoin().get()
    val isDarkTheme by themeManager.isDarkThemeFlow.collectAsStateWithLifecycle(
        initialValue = false
    )

    SoloGoTheme(darkTheme = isDarkTheme) {
        SoloGoNavHost()
    }
}