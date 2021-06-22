package com.flamyoad.honnoki.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UiPreference(private val dataStore: DataStore<Preferences>) {
    private val NIGHT_MODE_ENABLED = booleanPreferencesKey("night_mode_enabled")

    val nightModeEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        return@map prefs[NIGHT_MODE_ENABLED] ?: false
    }

    suspend fun setNightMode(enabled: Boolean) {
        dataStore.edit {
            it[NIGHT_MODE_ENABLED] = enabled
        }
    }
}