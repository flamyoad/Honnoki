package com.flamyoad.honnoki.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.Chapter
import com.flamyoad.honnoki.data.model.Page
import com.flamyoad.honnoki.data.model.State
import com.flamyoad.honnoki.repository.ChapterRepository
import com.flamyoad.honnoki.repository.OverviewRepository
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.ui.reader.model.LoadType
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

@ExperimentalPagingApi
class ReaderViewModel(
    private val db: AppDatabase,
    private val chapterRepo: ChapterRepository,
    private val overviewRepo: OverviewRepository,
    private val applicationScope: CoroutineScope,
    private val baseSource: BaseSource
) : ViewModel() {

    private val mangaOverviewId = MutableStateFlow(-1L)

    val mangaOverview = mangaOverviewId
        .flatMapLatest { db.mangaOverviewDao().getById(it) }
        .flowOn(Dispatchers.IO)

    val chapterList = mangaOverviewId
        .flatMapLatest { db.chapterDao().getAscByOverviewId(it) }
        .flowOn(Dispatchers.IO)

    private val sideKickVisibility = MutableStateFlow(false)
    fun sideKickVisibility() = sideKickVisibility.asStateFlow()

    private val currentChapterShown = MutableStateFlow(Chapter.empty())
    fun currentChapterShown() = currentChapterShown.asStateFlow()

    val currentChapter get() = currentChapterShown.value

    // Not used for scrolling. Use #pageNumberScrolledBySeekbar instead for scrolling purpose
    private val currentPageNumber = MutableStateFlow(0)
    fun currentPageNumber() = currentPageNumber.asStateFlow()

    val totalPageNumber = currentChapterShown
        .filter { it.id != null }
        .flatMapLatest { db.chapterDao().getTotalPages(requireNotNull(it.id)) }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val currentPageIndicator = currentPageNumber.combine(totalPageNumber) { current, total ->
        "$current / $total"
    }

    private val pageNumberScrolledBySeekbar = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 1
    )

    fun pageNumberScrolledBySeekbar() = pageNumberScrolledBySeekbar.asSharedFlow()

    private val pageList = MutableStateFlow<List<ReaderPage>>(emptyList())
    fun pageList() = pageList.asStateFlow()

    private val showBottomLoadingIndicator = MutableStateFlow(false)
    fun showBottomLoadingIndicator() = showBottomLoadingIndicator.asStateFlow()

    private val failedToLoadNextChapter = MutableStateFlow(false)
    fun failedToLoadNextChapter() = failedToLoadNextChapter.asStateFlow()

    private var loadPrevChapterJob: Job? = null
    private var loadNextChapterJob: Job? = null

    private val loadCompletionStatusByChapterId = ConcurrentHashMap<Long, Boolean>()

    var currentScrollPosition: Int = -1

    fun fetchChapterImages(chapterId: Long, loadType: LoadType): Job {
        // Skip loading this chapter if it has been loaded before. Returns a completed job by default
        if (loadCompletionStatusByChapterId[chapterId] == true) {
            return Job().apply { complete() }
        }

        if (loadType == LoadType.NEXT) {
            showBottomLoadingIndicator.value = true
        }

        return viewModelScope.launch(Dispatchers.IO) {
            val chapter = db.chapterDao().get(chapterId)
                ?: throw IllegalArgumentException("Chapter id is null")

            val result = baseSource.getImages(chapter.link)
            when (result) {
                is State.Success -> {
                    processChapterImages(chapter, result.value, loadType)
                    markChapterAsRead(chapter)
                }
                is State.Error -> failedToLoadNextChapter.value = true
            }

            // Hides the loading indicator regardless success or failed to load next chapter
            showBottomLoadingIndicator.value = false
        }
    }

    private fun processChapterImages(chapter: Chapter, pages: List<Page>, loadType: LoadType) {
        val chapterId = chapter.id ?: throw IllegalArgumentException("Chapter id is null")

        val pagesWithoutChapterId = pages.map {
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
                val list = pagesWithChapterId.map { ReaderPage.Value(it) } as MutableList<ReaderPage>
                list.add(ReaderPage.Ads(chapterId))
                existingList.addAll(0, list)
            }
            LoadType.NEXT -> {
                existingList.addAll(pagesWithChapterId.map { ReaderPage.Value(it) })
                existingList.add(ReaderPage.Ads(chapterId))
            }
        }

        // Submits pages from the newly loaded chapter to adapter
        pageList.value = existingList

        // Marks the chapter as completed to prevent duplicate loading
        loadCompletionStatusByChapterId.put(chapter.id, true)
    }

    private suspend fun markChapterAsRead(chapter: Chapter) {
        val overviewId = mangaOverviewId.value
        if (overviewId == -1L) return

        chapterRepo.markChapterAsRead(chapter)
        overviewRepo.updateLastReadChapter(chapter, overviewId)
    }

    fun saveLastReadPage(pageNumber: Int) {
        val overviewId = mangaOverviewId.value ?: return
        applicationScope.launch(Dispatchers.IO) {
            overviewRepo.updateLastReadPage(pageNumber, overviewId)
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

        failedToLoadNextChapter.value = false

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
        // One-based numbering is used because we are checking against the page number in db rows
        pageNumberScrolledBySeekbar.tryEmit(1)
    }

    fun goToLastPage() {
        val lastPage = totalPageNumber.value
        pageNumberScrolledBySeekbar.tryEmit(lastPage)
    }
}