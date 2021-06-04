package com.flamyoad.honnoki.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.repository.BaseMangaRepository
import com.flamyoad.honnoki.repository.MangakalotRepository
import com.flamyoad.honnoki.ui.reader.model.LoadType
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class ReaderViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)

    private var mangaRepo: BaseMangaRepository = MangakalotRepository(db, app.applicationContext)

    private val mangaOverviewId = MutableStateFlow(-1L)

    val chapterList = mangaOverviewId
        .flatMapLatest { db.chapterDao().getByOverviewId(it) }

    private val sideKickVisibility = MutableStateFlow(false)
    fun sideKickVisibility(): StateFlow<Boolean> = sideKickVisibility

    private val currentChapterShown = MutableStateFlow(Chapter.empty())
    fun currentChapterShown(): Flow<Chapter> = currentChapterShown

    val currentChapter get() = currentChapterShown.value

    private val currentPageNumber = MutableStateFlow(0)
    fun currentPageNumber(): StateFlow<Int> = currentPageNumber

    val totalPageNumber = currentChapterShown.flatMapLatest {
        if (it.id == null) return@flatMapLatest flowOf(0)
        db.chapterDao().getTotalPages(it.id)
    }

    val currentPageIndicator = currentPageNumber.combine(totalPageNumber) { current, total ->
        "Page: $current / $total"
    }

    private val pageNumberScrolledBySeekbar = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 1
    )
    fun pageNumberScrolledBySeekbar(): SharedFlow<Int> = pageNumberScrolledBySeekbar.asSharedFlow()

    private val pageList = MutableStateFlow<List<ReaderPage>>(emptyList())
    fun pageList(): Flow<List<ReaderPage>> = pageList

    private val showBottomLoadingIndicator = MutableStateFlow(false)
    fun showBottomLoadingIndicator(): StateFlow<Boolean> = showBottomLoadingIndicator

    private var fetchMangaJob: Job? = null

    var currentScrollPosition: Int = -1

    fun fetchManga(chapterId: Long, loadType: LoadType) {
        fetchMangaJob?.cancel()

        if (loadType == LoadType.NEXT) {
            showBottomLoadingIndicator.value = true
        }

        fetchMangaJob = viewModelScope.launch(Dispatchers.IO) {
            val chapter = db.chapterDao().get(chapterId) ?: throw IllegalArgumentException("")
            val result = mangaRepo.getImages(chapter.link)

            if (result is State.Success) {
                val pagesWithoutChapterId = result.value.map {
                    it.copy(chapterId = chapterId)
                }

                db.pageDao().insertAll(pagesWithoutChapterId)
                val pagesWithChapterId = db.pageDao().getAllFromChapter(chapterId)

                val existingList = pageList.value?.toMutableList() ?: mutableListOf()

                when (loadType) {
                    LoadType.INITIAL -> {
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
        viewModelScope.launch(Dispatchers.IO) {
            val overviewId = mangaOverviewId.value
            val currentChapterNumber = currentChapterShown.value.number

            val prevChapter = db.chapterDao().getPreviousChapter(overviewId, currentChapterNumber)
                ?: return@launch
            val chapterId = prevChapter.id ?: return@launch
            fetchManga(chapterId, LoadType.PREV)
        }
    }

    fun loadNextChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            val overviewId = mangaOverviewId.value
            val currentChapterNumber = currentChapterShown.value.number

            val nextChapter =
                db.chapterDao().getNextChapter(overviewId, currentChapterNumber) ?: return@launch
            val chapterId = nextChapter.id ?: return@launch
            fetchManga(chapterId, LoadType.NEXT)
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
}