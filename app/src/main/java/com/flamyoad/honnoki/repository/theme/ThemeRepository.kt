package com.flamyoad.honnoki.repository.theme

import com.flamyoad.honnoki.data.UiMode
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getUiMode(): Flow<UiMode>
    fun getUiModeBlocking(): UiMode
    fun getCompletedOnboarding(): Boolean

    suspend fun setUiMode(uiMode: UiMode)
    suspend fun setCompletedOnboarding(hasCompleted: Boolean)
}