package com.flamyoad.honnoki.repository.theme

import com.flamyoad.honnoki.data.UiMode
import com.flamyoad.honnoki.data.preference.UiPreference
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class ThemeRepositoryImpl(private val uiPreference: UiPreference) :
    ThemeRepository {

    override fun getUiMode(): UiMode = runBlocking {
        val nightModeEnabled = uiPreference.nightModeEnabled.firstOrNull()
        return@runBlocking if (nightModeEnabled == true) {
            UiMode.DARK
        } else {
            UiMode.LIGHT
        }
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