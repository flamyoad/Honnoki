package com.flamyoad.honnoki.repository.theme

import com.flamyoad.honnoki.data.UiMode

interface ThemeRepository {
    fun getUiMode(): UiMode
    fun getCompletedOnboarding(): Boolean

    suspend fun setUiMode(uiMode: UiMode)
    suspend fun setCompletedOnboarding(hasCompleted: Boolean)
}