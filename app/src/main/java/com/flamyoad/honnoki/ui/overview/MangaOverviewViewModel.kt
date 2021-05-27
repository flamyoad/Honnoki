package com.flamyoad.honnoki.ui.overview

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.*
import com.flamyoad.honnoki.repository.BaseMangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

@ExperimentalPagingApi
class MangaOverviewViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)

    private lateinit var mangaRepo: BaseMangaRepository

    private val mangaOverviewId = MutableStateFlow(-1L)

    val mangaOverview = mangaOverviewId
        .flatMapLatest {
            if (it == -1L) return@flatMapLatest flowOf(MangaOverview.empty())
            return@flatMapLatest db.mangaOverviewDao().getById(it)
        }
        .asLiveData()

    val genreList: LiveData<List<Genre>> = mangaOverviewId
        .flatMapLatest { return@flatMapLatest db.genreDao().getByOverviewId(it) }
        .asLiveData()

    val authorList: LiveData<List<Author>> = mangaOverviewId
        .flatMapLatest { return@flatMapLatest db.authorDao().getByOverviewId(it) }
        .asLiveData()

    private var chapterList = MutableLiveData<State<List<Chapter>>>(State.Loading)
    fun chapterList(): LiveData<State<List<Chapter>>> = chapterList

    private var isBookmarked = MutableLiveData<Boolean>()
    fun isBookmarked(): LiveData<Boolean> = isBookmarked

    fun initializeAll(url: String, sourceName: String) {
        val source = try {
            Source.valueOf(sourceName)
        } catch (e: IllegalArgumentException) {
            return
        }

        mangaRepo = BaseMangaRepository.get(source, db, app.applicationContext)
        loadMangaOverview(url)
        loadChapterList(url)
    }

    fun loadMangaOverview(url: String) {
        viewModelScope.launch {
            when (val overview = mangaRepo.getMangaOverview(url)) {
                is State.Success -> {
                    val overviewId = db.mangaOverviewDao().insert(overview.value)
                    mangaOverviewId.value = overviewId
                    loadGenres(url, overviewId)
                    loadAuthors(url, overviewId)
                }
            }
        }
    }

    private suspend fun loadGenres(url: String, overviewId: Long) {
        when (val genreList = mangaRepo.getGenres(url)) {
            is State.Success -> {
                val genreListWithId = genreList.value.map {
                    it.copy(mangaOverviewId = overviewId)
                }
                db.genreDao().insertAll(genreListWithId)
            }
        }
    }

    private suspend fun loadAuthors(url: String, overviewId: Long) {
        when (val authorList = mangaRepo.getAuthors(url)) {
            is State.Success -> {
                val authorListWithId = authorList.value.map {
                    it.copy(mangaOverviewId = overviewId)
                }
                db.authorDao().insertAll(authorListWithId)
            }
        }
    }

    fun loadChapterList(url: String) {
        viewModelScope.launch {
            val chapters = mangaRepo.getChapterList(url)
            chapterList.postValue(chapters)
        }
    }

    fun sortChapterList(isAscending: Boolean) {
        if (chapterList.value !is State.Success) {
            return
        }

        val oldChapterList = chapterList.value
        val oldChapterListItems = (oldChapterList as State.Success).value

        val x = Random.nextBoolean()

        viewModelScope.launch(Dispatchers.Default) {
            val newChapterListItems = if (x) {
                oldChapterListItems.sortedBy { chapter -> chapter.title }
            } else {
                oldChapterListItems.sortedByDescending { chapter -> chapter.title }
            }
            chapterList.postValue(State.Success(newChapterListItems))
        }
    }

    fun toggleBookmarkStatus() {
        val status = isBookmarked.value
        isBookmarked.value = if (status == null) {
            true
        } else {
            !status
        }
    }
}