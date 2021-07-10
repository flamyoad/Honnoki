package com.flamyoad.honnoki.ui.reader

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.MangaOverview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@ExperimentalPagingApi
class VerticalScrollingReaderViewModel(
    private val db: AppDatabase
) : ViewModel() {

    private val _pullToRefreshEnabled = MutableStateFlow(false)
    val disablePullToRefresh = _pullToRefreshEnabled.asStateFlow()

    fun setPullToRefreshEnabled(isEnabled: Boolean) {
        _pullToRefreshEnabled.value = isEnabled
    }

    suspend fun getMangaOverview(overviewId: Long): MangaOverview {
        return db.mangaOverviewDao().getByIdBlocking(overviewId)
    }
}