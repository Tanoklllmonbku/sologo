// MainActivity.kt
package com.sologo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sologo.app.presentation.navigation.SoloGoNavHost
import com.sologo.app.presentation.theme.SoloGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoloGoTheme {
                SoloGoNavHost()
            }
        }
    }
}