package com.flamyoad.honnoki.ui.overview

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.*
import com.flamyoad.honnoki.repository.BaseMangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MangaOverviewViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)

    private lateinit var mangaRepo: BaseMangaRepository

    private val mangaOverviewId = MutableStateFlow(-1L)

    val overviewId get() = mangaOverviewId.value

    val mangaOverview = mangaOverviewId
        .flatMapLatest {
            if (it == -1L) return@flatMapLatest flowOf(MangaOverview.empty())
            return@flatMapLatest db.mangaOverviewDao().getById(it)
        }
        .asLiveData()

    val genreList: LiveData<List<Genre>> = mangaOverviewId
        .flatMapLatest { db.genreDao().getByOverviewId(it) }
        .asLiveData()

    val authorList: LiveData<List<Author>> = mangaOverviewId
        .flatMapLatest { db.authorDao().getByOverviewId(it) }
        .asLiveData()

    val chapterList: LiveData<State<List<Chapter>>> = mangaOverviewId
        .onStart { flowOf(State.Loading) }
        .flatMapLatest { db.chapterDao().getByOverviewId(it) }
        .flatMapLatest {
            if (it.isNullOrEmpty()) {
                flowOf(State.Loading)
            } else {
                flowOf(State.Success(it))
            }
        }
        .asLiveData()

    val hasBeenBookmarked = mangaOverviewId
        .flatMapLatest { db.mangaOverviewDao().hasBeenBookmarked(it) }
        .asLiveData()

    fun initializeAll(url: String, sourceName: String) {
        val source = try {
            Source.valueOf(sourceName)
        } catch (e: IllegalArgumentException) {
            return
        }

        mangaRepo = BaseMangaRepository.get(source, db, app.applicationContext)
        loadMangaOverview(url)
    }

    fun loadMangaOverview(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Load manga overview from database
            val overviewFromDb = db.mangaOverviewDao().getByLinkBlocking(url)
            if (overviewFromDb != null) {
                mangaOverviewId.value = requireNotNull(overviewFromDb.id)
                refreshChapterList(url, overviewFromDb.id)
                return@launch
            }

            // Otherwise, load manga overview from network
            when (val overview = mangaRepo.getMangaOverview(url)) {
                is State.Success -> {
                    val overviewId = db.mangaOverviewDao().insert(overview.value)
                    mangaOverviewId.value = overviewId
                    refreshChapterList(url, overviewId)
                    refreshGenres(url, overviewId)
                    refreshAuthors(url, overviewId)
                }
            }
        }
    }

    private suspend fun refreshGenres(url: String, overviewId: Long) {
        when (val genreList = mangaRepo.getGenres(url)) {
            is State.Success -> {
                val genreListWithId = genreList.value.map {
                    it.copy(mangaOverviewId = overviewId)
                }
                db.genreDao().insertAll(genreListWithId)
            }
        }
    }

    private suspend fun refreshAuthors(url: String, overviewId: Long) {
        when (val authorList = mangaRepo.getAuthors(url)) {
            is State.Success -> {
                val authorListWithId = authorList.value.map {
                    it.copy(mangaOverviewId = overviewId)
                }
                db.authorDao().insertAll(authorListWithId)
            }
        }
    }

    private suspend fun refreshChapterList(url: String, overviewId: Long) {
        when (val chapterList = mangaRepo.getChapterList(url)) {
            is State.Success -> {
                val chapterListWithId = chapterList.value.map {
                    it.copy(mangaOverviewId = overviewId)
                }
                db.chapterDao().insertAll(chapterListWithId)
            }
        }
    }

    fun sortChapterList(isAscending: Boolean) {
//        if (chapterList.value !is State.Success) {
//            return
//        }
//
//        val oldChapterList = chapterList.value
//        val oldChapterListItems = (oldChapterList as State.Success).value
//
//        val x = Random.nextBoolean()
//
//        viewModelScope.launch(Dispatchers.Default) {
//            val newChapterListItems = if (x) {
//                oldChapterListItems.sortedBy { chapter -> chapter.title }
//            } else {
//                oldChapterListItems.sortedByDescending { chapter -> chapter.title }
//            }
//            chapterList.postValue(State.Success(newChapterListItems))
//        }
    }
}