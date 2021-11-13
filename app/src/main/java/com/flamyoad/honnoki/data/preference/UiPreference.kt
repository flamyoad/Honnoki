package com.flamyoad.honnoki.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.flamyoad.honnoki.utils.extensions.getValue
import kotlinx.coroutines.flow.Flow

class UiPreference(private val dataStore: DataStore<Preferences>) {
    private val NIGHT_MODE_ENABLED = booleanPreferencesKey("night_mode_enabled")
    private val HAS_COMPLETED_ONBOARDING =
        booleanPreferencesKey("has_completed_onboarding")

    val nightModeEnabled: Flow<Boolean> =
        dataStore.getValue(NIGHT_MODE_ENABLED, defaultValueIfNull = false)

    suspend fun setNightMode(enabled: Boolean) = dataStore.edit {
        it[NIGHT_MODE_ENABLED] = enabled
    }

    val hasCompletedOnboarding: Flow<Boolean> =
        dataStore.getValue(HAS_COMPLETED_ONBOARDING, defaultValueIfNull = false)

    suspend fun setHasCompletedOnboarding(enabled: Boolean) = dataStore.edit {
        it[HAS_COMPLETED_ONBOARDING] = enabled
    }

}