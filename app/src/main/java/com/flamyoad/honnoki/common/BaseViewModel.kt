package com.flamyoad.honnoki.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

open class BaseViewModel : ViewModel() {

    // Can use Flow.toLiveData() if want
    fun <T> Flow<T>.toStateFlow(
        initialValue: T,
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(),
    ) = stateIn(viewModelScope, sharingStarted, initialValue)

    fun <T> Flow<T>.toSharedFlow(
        sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(),
        replay: Int,
    ) = shareIn(viewModelScope, sharingStarted, replay)
}