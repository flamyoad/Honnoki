package com.flamyoad.honnoki.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalPagingApi
class VerticalScrollingReaderViewModel() : ViewModel() {

    private val _pullToRefreshEnabled = MutableStateFlow(false)
    val disablePullToRefresh = _pullToRefreshEnabled.asStateFlow()

    fun setPullToRefreshEnabled(isEnabled: Boolean) {
        _pullToRefreshEnabled.value = isEnabled
    }
}