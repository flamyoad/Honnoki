package com.flamyoad.honnoki

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private val actionModeEnabled = MutableStateFlow(false)
    fun actionModeEnabled() = actionModeEnabled.asStateFlow()

    fun setActionMode(isEnabled: Boolean) {
        actionModeEnabled.value = isEnabled
    }
}