package com.flamyoad.honnoki.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.lang.IllegalArgumentException

@ExperimentalPagingApi
class ReaderViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)

    private var mangaRepo: BaseMangaRepository = MangakalotRepository(db, app.applicationContext)

    private val mangaOverviewId = MutableStateFlow(-1L)

    val overviewId get() = mangaOverviewId.value

    val chapterList = mangaOverviewId
        .flatMapLatest { db.chapterDao().getByOverviewId(it) }

    private val sideKickVisibility = MutableStateFlow(false)
    fun sideKickVisibility(): StateFlow<Boolean> = sideKickVisibility

    private val currentPageNumber = MutableStateFlow(0)
    fun currentPage(): StateFlow<Int> = currentPageNumber

    private val totalPageNumber = MutableStateFlow(0)
    fun totalPages(): StateFlow<Int> = totalPageNumber

    val currentPageIndicator = currentPageNumber.combine(totalPageNumber) { current, total ->
        "Page: ${current + 1} / ${total + 1}"
    }

    private val pageNumberScrolledBySeekbar = MutableStateFlow(-1)
    fun pageNumberScrolledBySeekbar(): Flow<Int> = pageNumberScrolledBySeekbar

    private val pageList = MutableLiveData<List<ReaderPage>>()
    fun pageList(): LiveData<List<ReaderPage>> = pageList

    private val showBottomLoadingIndicator = MutableStateFlow(false)
    fun showBottomLoadingIndicator(): StateFlow<Boolean> = showBottomLoadingIndicator

    private val currentChapterShown = MutableStateFlow(Chapter.empty())
    fun currentChapterShown(): Flow<Chapter> = currentChapterShown

    private var fetchMangaJob: Job? = null

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
                        val list = pagesWithChapterId.map { ReaderPage.Value(it) } as MutableList<ReaderPage>
                        list.add(ReaderPage.Ads(chapterId))
                        existingList.addAll(0, list)
                    }
                    LoadType.NEXT -> {
                        existingList.addAll(pagesWithChapterId.map { ReaderPage.Value(it) })
                        existingList.add(ReaderPage.Ads(chapterId))
                    }
                }

                pageList.postValue(existingList)
                currentChapterShown.value = chapter

                showBottomLoadingIndicator.value = false
            }
        }
    }

    fun fetchChapterList(overviewId: Long) {
        mangaOverviewId.value = overviewId
    }

    fun setSideKickVisibility(isVisible: Boolean) {
        sideKickVisibility.value = isVisible
    }

    fun setCurrentPage(number: Int) {
        currentPageNumber.value = number
    }

    fun setTotalPages(number: Int) {
        totalPageNumber.value = number - 1
    }

    fun setSeekbarScrolledPosition(position: Int) {
        pageNumberScrolledBySeekbar.tryEmit(position)
    }

    fun setCurrentChapter(chapter: Chapter) {
        currentChapterShown.value = chapter
    }

    fun loadPreviousChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentChapterNumber = currentChapterShown.value.number
            val prevChapter = db.chapterDao().getPreviousChapter(overviewId, currentChapterNumber) ?: return@launch
            val chapterId = prevChapter.id ?: return@launch
            fetchManga(chapterId, LoadType.PREV)
        }
    }

    fun loadNextChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentChapterNumber = currentChapterShown.value.number
            val nextChapter = db.chapterDao().getNextChapter(overviewId, currentChapterNumber) ?: return@launch
            val chapterId = nextChapter.id ?: return@launch
            fetchManga(chapterId, LoadType.NEXT)
        }
    }
}