package com.flamyoad.honnoki.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.data.preference.SourcePreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class HomeViewModel(private val sourcePref: SourcePreference) : ViewModel() {

    private val shouldShrinkFab = MutableLiveData(false)
    fun shouldShrinkFab(): LiveData<Boolean> = shouldShrinkFab

    val chosenSource = sourcePref.source
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    fun setShouldShrinkFab(boolean: Boolean) {
        shouldShrinkFab.value = boolean
    }

    fun switchSource(source: Source) {
        viewModelScope.launch {
            sourcePref.editSource(source)
        }
    }
}