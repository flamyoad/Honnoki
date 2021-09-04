package com.flamyoad.honnoki.ui.overview

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.room.withTransaction
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.mapper.mapToDomain
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.repository.ChapterRepository
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.ui.overview.model.ChapterListSort
import com.flamyoad.honnoki.ui.overview.model.LanguageFilter
import com.flamyoad.honnoki.ui.overview.model.ReaderChapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MangaOverviewViewModel(
    private val db: AppDatabase,
    private val baseSource: BaseSource,
    private val chapterRepo: ChapterRepository
) : ViewModel() {

    private var loadingJob: Job? = null

    private val mangaOverviewId = MutableStateFlow(-1L)

    val mangaOverview = mangaOverviewId
        .filter { it != -1L }
        .flatMapLatest { db.mangaOverviewDao().getById(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MangaOverview.empty())

    val overview get() = mangaOverview.value

    val genreList: LiveData<List<Genre>> = mangaOverviewId
        .flatMapLatest { db.genreDao().getByOverviewId(it) }
        .asLiveData()

    val authorList: LiveData<List<Author>> = mangaOverviewId
        .flatMapLatest { db.authorDao().getByOverviewId(it) }
        .asLiveData()

    private val chapterListSortType = MutableStateFlow(ChapterListSort.DESC)

    // todo: Move the default lang to datastore, if got time ;d
    private val selectedLanguage = MutableStateFlow(LanguageFilter.empty())

    val languageList: LiveData<List<LanguageFilter>> = mangaOverviewId
        .flatMapLatest { id -> db.chapterDao().getAvailableLanguages(id) }
        .combine(selectedLanguage) { languageList, selectedLanguage ->
            Pair(
                languageList,
                selectedLanguage
            )
        }
        .map { (languageList, selectedLanguage) ->
            languageList.map {
                LanguageFilter(it, isSelected = (it == selectedLanguage.locale))
            }
        }
        .asLiveData()

    private val noChaptersFound = MutableStateFlow(false)
    fun noChaptersFound() = noChaptersFound.asStateFlow()

    val chapterList: StateFlow<State<List<ReaderChapter>>> = mangaOverviewId
        .onStart { flowOf(State.Loading) }
        .combine(chapterListSortType) { id, sortType -> Pair(id, sortType) }
        .combine(selectedLanguage) { (id, sortType), lang -> Triple(id, sortType, lang) }
        .flatMapLatest { (id, sortType, lang) ->
            when (sortType) {
                ChapterListSort.ASC -> {
                    if (lang == LanguageFilter.empty()) {
                        db.chapterDao().getAscByOverviewId(id)
                    } else {
                        db.chapterDao().getAscByOverviewIdFromLanguage(id, lang.locale)
                    }
                }
                ChapterListSort.DESC -> {
                    if (lang == LanguageFilter.empty()) {
                        db.chapterDao().getDescByOverviewId(id)
                    } else {
                        db.chapterDao().getDescByOverviewIdFromLanguage(id, lang.locale)
                    }
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .combine(mangaOverview) { chapterList, overview ->
            chapterList.mapToDomain(overview)
        }
        .flatMapLatest {
            if (it.isNullOrEmpty()) {
                flowOf(State.Loading)
            } else {
                flowOf(State.Success(it))
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), State.Loading)

    val hasBeenBookmarked = mangaOverviewId
        .flatMapLatest { db.mangaOverviewDao().hasBeenBookmarked(it) }
        .asLiveData()

    fun loadMangaOverview(url: String) {
        if (loadingJob?.isActive == true) {
            return
        }

        noChaptersFound.value = false

        loadingJob = viewModelScope.launch(Dispatchers.IO) {
            // Load manga overview from database
            val overviewFromDb = db.mangaOverviewDao().getByLinkBlocking(url)
            if (overviewFromDb != null) {
                mangaOverviewId.value = requireNotNull(overviewFromDb.id)
                refreshChapterList(url, overviewFromDb.id)
                return@launch
            }

            // Otherwise, load manga overview from network
            when (val overview = baseSource.getMangaOverview(url)) {
                is State.Success -> {
                    val overviewId = db.mangaOverviewDao().insert(overview.value)
                    mangaOverviewId.value = overviewId

                    refreshAuthors(url, mangaOverviewId.value)
                    refreshGenres(url, mangaOverviewId.value)
                    refreshChapterList(url, mangaOverviewId.value)
                }
            }
        }
    }

    private suspend fun refreshGenres(url: String, overviewId: Long) {
        when (val genreList = baseSource.getGenres(url)) {
            is State.Success -> {
                val genreListWithId = genreList.value.map {
                    it.copy(mangaOverviewId = overviewId)
                }
                db.withTransaction {
                    db.genreDao().insertAll(genreListWithId)
                }
            }
        }
    }

    private suspend fun refreshAuthors(url: String, overviewId: Long) {
        when (val authorList = baseSource.getAuthors(url)) {
            is State.Success -> {
                val authorListWithId = authorList.value.map {
                    it.copy(mangaOverviewId = overviewId)
                }
                db.withTransaction {
                    db.authorDao().insertAll(authorListWithId)
                }
            }
        }
    }

    private suspend fun refreshChapterList(url: String, overviewId: Long) {
        when (val chapterList = baseSource.getChapterList(url)) {
            is State.Success -> {
                if (chapterList.value.isEmpty()) {
                    noChaptersFound.value = true
                } else {
                    val chapterListWithId = chapterList.value.map {
                        it.copy(mangaOverviewId = overviewId)
                    }
                    db.withTransaction {
                        db.chapterDao().insertAll(chapterListWithId)
                    }
                }
            }
        }
    }

    fun clearExistingChaptersAndReload(url: String) {
        val overview = mangaOverview.value
        if (overview == MangaOverview.empty()) return

        viewModelScope.launch(Dispatchers.IO) {
            db.withTransaction { chapterRepo.clearExistingChapters(overview) }
            loadMangaOverview(url)
        }
    }

    fun toggleChapterListSort() {
        val prevValue = chapterListSortType.value
        chapterListSortType.value = when (prevValue) {
            ChapterListSort.ASC -> ChapterListSort.DESC
            ChapterListSort.DESC -> ChapterListSort.ASC
        }
    }

    fun setChapterLanguageFilter(languageFilter: LanguageFilter) {
        selectedLanguage.value = languageFilter
    }

    fun getFirstChapter(overviewId: Long): Chapter? {
        return db.chapterDao().getFirst(overviewId)
    }
}