package com.flamyoad.honnoki.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.data.preference.DownloadPreference
import com.flamyoad.honnoki.data.preference.ReaderPreference
import com.flamyoad.honnoki.data.preference.SourcePreference
import com.flamyoad.honnoki.data.preference.UiPreference
import com.flamyoad.honnoki.download.model.NetworkType
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.options.model.MangadexQualityModeOption
import com.flamyoad.honnoki.ui.options.model.NetworkTypeOption
import com.flamyoad.honnoki.ui.options.model.SourceOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val uiPrefs: UiPreference,
    private val sourcePrefs: SourcePreference,
    private val readerPrefs: ReaderPreference,
    private val downloadPrefs: DownloadPreference,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    val nightModeEnabled = uiPrefs.nightModeEnabled
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    val preferredSource: Flow<Source> = sourcePrefs.homeSource

    val preferredMangadexQuality: Flow<MangadexQualityMode> = readerPrefs.mangadexQualityMode

    val showExtraSpaceAtBottomIndicator: Flow<Boolean> = readerPrefs.extraSpaceAtBottomIndicator

    val preferredNetworkType: Flow<NetworkType> = downloadPrefs.getPreferredNetworkType()

    val sourceOptionList: StateFlow<List<SourceOption>> = preferredSource
        .map { selectedSource ->
            Source.values()
                .filter { it.isEnabled }
                .map { SourceOption(source = it, isSelected = it == selectedSource) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val mangadexQualityOptionList: StateFlow<List<MangadexQualityModeOption>> = preferredMangadexQuality
        .map { selectedQuality ->
            MangadexQualityMode.values()
                .map { MangadexQualityModeOption(mode = it, isSelected = it == selectedQuality) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val preferredNetworkTypeList: StateFlow<List<NetworkTypeOption>> = preferredNetworkType
        .map { selected ->
            NetworkType.values()
                .map { NetworkTypeOption(it, isSelected = it == selected) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun setNightMode(enabled: Boolean) {
        viewModelScope.launch {
            uiPrefs.setNightMode(enabled)
        }
    }

    fun setExtraSpaceAtBottomIndicator(enabled: Boolean) {
        applicationScope.launch {
            readerPrefs.editExtraSpaceAtBottomIndicator(enabled)
        }
    }

    fun setHomePreferredSource(source: Source) {
        applicationScope.launch {
            sourcePrefs.editHomeSource(source)
        }
    }

    fun setMangadexQuality(mode: MangadexQualityMode) {
        applicationScope.launch {
            readerPrefs.editMangadexQualityMode(mode)
        }
    }

    fun setDownloadPreferredNetwork(networkType: NetworkType) {
        applicationScope.launch {
            downloadPrefs.setPreferredNetworkType(networkType)
        }
    }
}