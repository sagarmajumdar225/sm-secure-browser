package com.smsecure.browser.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class ThemeManager(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("app_theme_mode")
    private val TOR_KEY = booleanPreferencesKey("tor_proxy_enabled")

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[THEME_KEY]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    val torEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[TOR_KEY] ?: false
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = mode.name
        }
    }

    suspend fun setTorEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[TOR_KEY] = enabled
        }
    }
}
