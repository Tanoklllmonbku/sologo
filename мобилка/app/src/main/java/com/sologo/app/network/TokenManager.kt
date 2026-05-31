package com.sologo.app.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sologo_auth")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")

        // Кэш токена в памяти
        private var cachedToken: String? = null
    }

    suspend fun saveToken(token: String) {
        cachedToken = token
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        cachedToken?.let { return it }
        return context.dataStore.data.first()[TOKEN_KEY].also {
            cachedToken = it
        }
    }

    suspend fun clearToken() {
        android.util.Log.d("AUTH_TEST", "clearToken: ДО - cachedToken = $cachedToken")
        cachedToken = null
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
        android.util.Log.d("AUTH_TEST", "clearToken: ПОСЛЕ - cachedToken = $cachedToken")
    }

    fun isLoggedIn(): Boolean {
        val result = cachedToken != null
        android.util.Log.d("AUTH_TEST", "isLoggedIn: $result, cachedToken = $cachedToken")
        return result
    }

    // Добавляем метод loadToken
    suspend fun loadToken() {
        cachedToken = context.dataStore.data.first()[TOKEN_KEY]
    }
}