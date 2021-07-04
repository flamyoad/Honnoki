package com.flamyoad.honnoki.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.data.preference.SourcePreference
import com.flamyoad.honnoki.data.preference.UiPreference
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.options.model.SourceOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val uiPrefs: UiPreference,
    private val sourcePrefs: SourcePreference,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    val nightModeEnabled = uiPrefs.nightModeEnabled
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    val preferredSource: Flow<Source> = sourcePrefs.homeSource

    val sourceOptionList: StateFlow<List<SourceOption>> = preferredSource
        .map { selectedSource ->
            Source.values()
                .filter { it.isEnabled }
                .map { SourceOption(source = it, isSelected = it == selectedSource) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setNightMode(enabled: Boolean) {
        viewModelScope.launch {
            uiPrefs.setNightMode(enabled)
        }
    }

    fun setHomePreferredSource(source: Source) {
        applicationScope.launch {
            sourcePrefs.editHomeSource(source)
        }
    }
}