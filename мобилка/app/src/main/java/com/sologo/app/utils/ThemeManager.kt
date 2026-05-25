package com.sologo.app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

class ThemeManager(private val context: Context) {

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    val isDarkThemeFlow: Flow<Boolean> = context.themeDataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false  // ← значение по умолчанию false
        }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }
}