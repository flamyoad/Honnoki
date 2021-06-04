package com.flamyoad.honnoki.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalPagingApi
class ReaderFrameViewModel(application: Application) : AndroidViewModel(application) {

    private val _pullToRefreshEnabled = MutableStateFlow(false)
    val disablePullToRefresh: StateFlow<Boolean> get() = _pullToRefreshEnabled

    fun setPullToRefreshEnabled(isEnabled: Boolean) {
        _pullToRefreshEnabled.value = isEnabled
    }
}