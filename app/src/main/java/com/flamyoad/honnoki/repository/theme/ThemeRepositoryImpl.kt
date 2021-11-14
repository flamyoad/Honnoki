package com.flamyoad.honnoki.repository.theme

import com.flamyoad.honnoki.data.UiMode
import com.flamyoad.honnoki.data.preference.UiPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class ThemeRepositoryImpl(private val uiPreference: UiPreference) :
    ThemeRepository {
    override fun getUiMode(): Flow<UiMode> {
        return uiPreference.nightModeEnabled.map {
            if (it) {
                return@map UiMode.DARK
            } else {
                UiMode.LIGHT
            }
        }
    }

    override fun getUiModeBlocking(): UiMode = runBlocking {
        getUiMode().firstOrNull() ?: UiMode.LIGHT
    }

    override fun getCompletedOnboarding(): Boolean = runBlocking {
        return@runBlocking uiPreference.hasCompletedOnboarding.firstOrNull()
            ?: false
    }

    override suspend fun setUiMode(uiMode: UiMode) {
        val nightModeEnabled = when (uiMode) {
            UiMode.DARK -> true
            UiMode.LIGHT -> false
            UiMode.SYSTEM_DEFAULT -> false
        }
        uiPreference.setNightMode(nightModeEnabled)
    }

    override suspend fun setCompletedOnboarding(hasCompleted: Boolean) {
        uiPreference.setHasCompletedOnboarding(hasCompleted)
    }
}