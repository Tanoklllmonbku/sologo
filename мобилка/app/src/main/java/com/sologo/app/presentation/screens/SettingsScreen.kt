package com.sologo.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sologo.app.presentation.theme.SoloGreen
import com.sologo.app.utils.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(themeManager: ThemeManager, onLogout: () -> Unit) {
    val isDarkTheme by themeManager.isDarkThemeFlow.collectAsStateWithLifecycle(initialValue = false)
    var localDarkTheme by remember { mutableStateOf(isDarkTheme) }
    LaunchedEffect(isDarkTheme) { localDarkTheme = isDarkTheme }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsCard(icon = Icons.Default.DarkMode, title = "Тёмная тема", description = "Включить тёмную тему оформления") {
                Switch(checked = localDarkTheme, onCheckedChange = { checked ->
                    localDarkTheme = checked
                    CoroutineScope(Dispatchers.IO).launch { themeManager.setDarkTheme(checked) }
                }, colors = androidx.compose.material3.SwitchDefaults.colors(checkedThumbColor = SoloGreen, checkedTrackColor = SoloGreen.copy(alpha = 0.5f)))
            }
            SettingsCard(icon = Icons.Default.Language, title = "Язык", description = "Русский") { Text("🇷🇺 RU", color = SoloGreen) }
            SettingsCard(icon = Icons.Default.Info, title = "О приложении", description = "Версия 1.0.0\nSoloGo - путешествуй самостоятельно") { Text("ℹ️", color = SoloGreen) }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.ExitToApp, null); Spacer(modifier = Modifier.height(4.dp)); Text("Выйти из аккаунта")
            }
        }
    }
}

@Composable
private fun SettingsCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, modifier: Modifier = Modifier, trailingContent: @Composable () -> Unit = {}) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = SoloGreen); Spacer(modifier = Modifier.height(12.dp))
                Column { Text(title, style = MaterialTheme.typography.titleMedium, color = SoloGreen); Text(description, style = MaterialTheme.typography.bodySmall) }
            }
            trailingContent()
        }
    }
}