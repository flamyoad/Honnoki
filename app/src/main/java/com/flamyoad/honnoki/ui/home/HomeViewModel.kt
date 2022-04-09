package com.flamyoad.honnoki.ui.home

import androidx.lifecycle.*
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.data.preference.SourcePreference
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val sourcePref: SourcePreference) : ViewModel() {

    private val shouldShrinkFab = MutableLiveData(false)
    fun shouldShrinkFab(): LiveData<Boolean> = shouldShrinkFab

    val chosenSource = sourcePref.homeSource
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun setShouldShrinkFab(boolean: Boolean) {
        shouldShrinkFab.value = boolean
    }

    fun getSource(): Source? {
        return chosenSource.replayCache.firstOrNull()
    }

    fun switchSource(source: Source) {
        viewModelScope.launch {
            sourcePref.editHomeSource(source)
        }
    }
}