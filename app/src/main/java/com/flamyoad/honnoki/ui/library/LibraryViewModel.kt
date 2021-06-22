package com.flamyoad.honnoki.ui.library

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryViewModel: ViewModel() {
    var actionModeEnabled: Boolean = false

    private val shouldCancelActionMode = MutableSharedFlow<Boolean>(
        replay = 0,
        extraBufferCapacity = 1
    )
    fun shouldCancelActionMode() = shouldCancelActionMode.asSharedFlow()

    fun notifyCancelActionMode() {
        shouldCancelActionMode.tryEmit(true)
    }
}