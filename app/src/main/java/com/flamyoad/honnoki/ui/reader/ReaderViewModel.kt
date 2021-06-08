package com.flamyoad.honnoki.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.Chapter
import com.flamyoad.honnoki.data.model.State
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.ui.reader.model.LoadType
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class ReaderViewModel(
    private val db: AppDatabase,
    private val baseSource: BaseSource
) : ViewModel() {

    private val mangaOverviewId = MutableStateFlow(-1L)

    val chapterList = mangaOverviewId
        .flatMapLatest { db.chapterDao().getAscByOverviewId(it) }
        .flowOn(Dispatchers.IO)

    private val sideKickVisibility = MutableStateFlow(false)
    fun sideKickVisibility() = sideKickVisibility.asStateFlow()

    private val currentChapterShown = MutableStateFlow(Chapter.empty())
    fun currentChapterShown() = currentChapterShown.asStateFlow()

    val currentChapter get() = currentChapterShown.value

    private val currentPageNumber = MutableStateFlow(0)
    fun currentPageNumber() = currentPageNumber.asStateFlow()

    val totalPageNumber = currentChapterShown
        .filter { it.id != null }
        .flatMapLatest { db.chapterDao().getTotalPages(requireNotNull(it.id)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val currentPageIndicator = currentPageNumber.combine(totalPageNumber) { current, total ->
        "$current / $total"
    }

    // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/
    // A default implementation of a shared flow that is created with MutableSharedFlow()
    // constructor function without parameters has no replay cache nor additional buffer
    private val pageNumberScrolledBySeekbar = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 1
    )

    fun pageNumberScrolledBySeekbar() = pageNumberScrolledBySeekbar.asSharedFlow()

    private val pageList = MutableStateFlow<List<ReaderPage>>(emptyList())
    fun pageList() = pageList.asStateFlow()

    private val showBottomLoadingIndicator = MutableStateFlow(false)
    fun showBottomLoadingIndicator() = showBottomLoadingIndicator.asStateFlow()

    private var loadPrevChapterJob: Job? = null
    private var loadNextChapterJob: Job? = null

    var currentScrollPosition: Int = -1

    fun fetchChapterImages(chapterId: Long, loadType: LoadType): Job {
        if (loadType == LoadType.NEXT) {
            showBottomLoadingIndicator.value = true
        }

        return viewModelScope.launch(Dispatchers.IO) {
            val chapter = db.chapterDao().get(chapterId)
                ?: throw IllegalArgumentException("Chapter id is null")

            val result = baseSource.getImages(chapter.link)

            if (result is State.Success) {
                val pagesWithoutChapterId = result.value.map {
                    it.copy(chapterId = chapterId)
                }

                db.pageDao().insertAll(pagesWithoutChapterId)
                val pagesWithChapterId = db.pageDao().getAllFromChapter(chapterId)

                val existingList = pageList.value.toMutableList()

                when (loadType) {
                    LoadType.INITIAL -> {
                        existingList.clear()
                        existingList.addAll(pagesWithChapterId.map { ReaderPage.Value(it) })
                        existingList.add(ReaderPage.Ads(chapterId))
                    }
                    LoadType.PREV -> {
                        val list =
                            pagesWithChapterId.map { ReaderPage.Value(it) } as MutableList<ReaderPage>
                        list.add(ReaderPage.Ads(chapterId))
                        existingList.addAll(0, list)
                    }
                    LoadType.NEXT -> {
                        existingList.addAll(pagesWithChapterId.map { ReaderPage.Value(it) })
                        existingList.add(ReaderPage.Ads(chapterId))
                    }
                }

                pageList.value = existingList
                currentChapterShown.value = chapter
                showBottomLoadingIndicator.value = false
            }
        }
    }

    fun loadPreviousChapter() {
        if (loadPrevChapterJob?.isActive == true) {
            return
        }

        loadPrevChapterJob = viewModelScope.launch(Dispatchers.IO) {
            val overviewId = mangaOverviewId.value
            val currentChapterNumber = currentChapterShown.value.number

            val prevChapter = db.chapterDao().getPreviousChapter(overviewId, currentChapterNumber)
                ?: return@launch
            val chapterId = prevChapter.id ?: return@launch

            fetchChapterImages(chapterId, LoadType.PREV)
                .join()
        }
    }

    fun loadNextChapter() {
        if (loadNextChapterJob?.isActive == true) {
            return
        }

        loadNextChapterJob = viewModelScope.launch(Dispatchers.IO) {
            val overviewId = mangaOverviewId.value
            val currentChapterNumber = currentChapterShown.value.number

            val nextChapter =
                db.chapterDao().getNextChapter(overviewId, currentChapterNumber) ?: return@launch
            val chapterId = nextChapter.id ?: return@launch

            fetchChapterImages(chapterId, LoadType.NEXT)
                .join()
        }
    }

    fun fetchChapterList(overviewId: Long) {
        mangaOverviewId.value = overviewId
    }

    fun setSideKickVisibility(isVisible: Boolean) {
        sideKickVisibility.value = isVisible
    }

    fun setCurrentPageNumber(number: Int) {
        currentPageNumber.value = number
    }

    fun setSeekbarScrolledPosition(position: Int) {
        pageNumberScrolledBySeekbar.tryEmit(position)
    }

    fun setCurrentChapter(chapter: Chapter) {
        currentChapterShown.value = chapter
    }

    fun goToFirstPage() {
        pageNumberScrolledBySeekbar.tryEmit(0)
    }

    fun goToLastPage() {
        val lastPage = totalPageNumber.value
        pageNumberScrolledBySeekbar.tryEmit(lastPage)
    }
}