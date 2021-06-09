package com.flamyoad.honnoki.ui.library

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryViewModel: ViewModel() {
    private val shouldCancelActionMode = MutableSharedFlow<Boolean>(
        replay = 1,
        extraBufferCapacity = 1
    )
    fun shouldCancelActionMode() = shouldCancelActionMode.asSharedFlow()

    fun notifyCancelActionMode() {
        shouldCancelActionMode.tryEmit(true)
    }
}