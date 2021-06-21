package com.flamyoad.honnoki.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.data.preference.UiPreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class OptionsViewModel(private val uiPrefs: UiPreference) : ViewModel() {
    val nightModeEnabled = uiPrefs.nightModeEnabled
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun setNightMode(enabled: Boolean) {
        viewModelScope.launch {
            uiPrefs.setNightMode(enabled)
        }
    }
}