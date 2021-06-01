package com.flamyoad.honnoki.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.db.AppDatabase
import kotlinx.coroutines.flow.*
import java.text.FieldPosition

@ExperimentalPagingApi
class ReaderViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)

    private val mangaOverviewId = MutableStateFlow(-1L)

    val overviewId get() = mangaOverviewId.value

    val chapterList = mangaOverviewId
        .flatMapLatest { db.chapterDao().getByOverviewId(it) }

    private val sideKickVisibility = MutableStateFlow(false)
    fun sideKickVisibility(): StateFlow<Boolean> = sideKickVisibility

    private val currentPage = MutableStateFlow(0)
    fun currentPage(): StateFlow<Int> = currentPage

    private val totalPages = MutableStateFlow(0)
    fun totalPages(): StateFlow<Int> = totalPages

    private val pageNumberScrolledBySeekbar = MutableStateFlow(-1)
    fun pageNumberScrolledBySeekbar(): Flow<Int> = pageNumberScrolledBySeekbar

    private val allowScrollToOtherChapters = MutableStateFlow(false)
    fun allowScrollToOtherChapters(): StateFlow<Boolean> = allowScrollToOtherChapters

    val currentPageIndicator = currentPage.combine(totalPages) { current, total ->
        "Page: ${current + 1} / ${total + 1}"
    }

    fun fetchChapterList(overviewId: Long) {
        mangaOverviewId.value = overviewId
    }

    fun setSideKickVisibility(isVisible: Boolean) {
        sideKickVisibility.value = isVisible
    }

    fun setCurrentPage(number: Int) {
        currentPage.value = number
    }

    fun setTotalPages(number: Int) {
        totalPages.value = number - 1
    }

    fun setSeekbarScrolledPosition(position: Int) {
        pageNumberScrolledBySeekbar.tryEmit(position)
    }
}