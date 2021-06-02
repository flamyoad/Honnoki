package com.flamyoad.honnoki.ui.reader

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.Page
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.repository.BaseMangaRepository
import com.flamyoad.honnoki.repository.MangakalotRepository
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

    private val pageNumberScrolledBySeekbar = MutableStateFlow(-1)
    fun pageNumberScrolledBySeekbar(): Flow<Int> = pageNumberScrolledBySeekbar

    val currentPageIndicator = currentPageNumber.combine(totalPageNumber) { current, total ->
        "Page: ${current + 1} / ${total + 1}"
    }

    private val pageList = MutableLiveData<List<ReaderPage>>()
    fun pageList(): LiveData<List<ReaderPage>> = pageList

    private val currentChapterShown = MutableStateFlow(Chapter.empty())
    fun currentChapterShown(): Flow<Chapter> = currentChapterShown

    val currentChapterId get() = currentChapterShown.value.id ?: -1

    private var fetchMangaJob: Job? = null

    fun fetchManga(chapterId: Long, addToFront: Boolean = false) {
        fetchMangaJob?.cancel()
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

                if (addToFront) {
                    existingList.addAll(0,  pagesWithChapterId.map { ReaderPage.Value(it) })
                } else {
                    existingList.addAll( pagesWithChapterId.map { ReaderPage.Value(it) })
                }
                existingList.add(ReaderPage.Ads(chapterId))

                pageList.postValue(existingList)
                currentChapterShown.value = chapter
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

    fun loadPreviousChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            val chapter = db.chapterDao().getPreviousChapter(overviewId, currentChapterId) ?: return@launch
            val chapterId = chapter.id ?: return@launch
            fetchManga(chapterId, addToFront = true)
        }
    }

    fun loadNextChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            val chapter = db.chapterDao().getNextChapter(overviewId, currentChapterId) ?: return@launch
            val chapterId = chapter.id ?: return@launch
            fetchManga(chapterId)
        }
    }
}