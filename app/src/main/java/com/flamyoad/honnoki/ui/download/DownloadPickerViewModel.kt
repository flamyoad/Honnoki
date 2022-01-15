package com.flamyoad.honnoki.ui.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.mapper.mapToDb
import com.flamyoad.honnoki.data.mapper.mapToDownloadChapters
import com.flamyoad.honnoki.repository.download.DownloadRepositoryImpl
import com.flamyoad.honnoki.ui.download.model.DownloadChapter
import com.flamyoad.honnoki.ui.overview.model.ChapterListSort
import com.flamyoad.honnoki.ui.overview.model.LanguageFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DownloadPickerViewModel(
    private val db: AppDatabase,
    private val downloadRepository: DownloadRepositoryImpl,
    private val applicationScope: CoroutineScope
) : ViewModel() {
    private val mangaOverviewId = MutableStateFlow(-1L)

    private val chapterListSortType = MutableStateFlow(ChapterListSort.DESC)

    private val selectedLanguage = MutableStateFlow(LanguageFilter.english())

    private var previousSelectedChapter: DownloadChapter? = null

    private val selectedChapters = MutableStateFlow<List<DownloadChapter>>(emptyList())
    fun selectedChapters() = selectedChapters.asStateFlow()

    val chapterList: StateFlow<State<List<DownloadChapter>>> = mangaOverviewId
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
        .combine(selectedChapters) { chapterList, selectedChapters ->
            chapterList.mapToDownloadChapters(selectedChapters)
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

    fun initChapterList(overviewId: Long) {
        mangaOverviewId.value = overviewId
    }

    fun toggleChapterListSort() {
        val prevValue = chapterListSortType.value
        chapterListSortType.value = when (prevValue) {
            ChapterListSort.ASC -> ChapterListSort.DESC
            ChapterListSort.DESC -> ChapterListSort.ASC
        }
    }

    fun selectLanguageLocale(languageLocale: String) {  
        selectedLanguage.value = LanguageFilter(locale = languageLocale, isSelected = false)
        previousSelectedChapter = null
    }

    fun onChapterPress(chapter: DownloadChapter) {
        val list = selectedChapters.value.toMutableList()
        if (chapter.isSelected) {
            val index = list.indexOfFirst { it.id == chapter.id }
            list.removeAt(index)
            previousSelectedChapter = null
        } else {
            list.add(chapter)
            previousSelectedChapter = chapter
        }
        selectedChapters.value = list
    }

    fun onChapterLongPress(chapter: DownloadChapter) {
        if (previousSelectedChapter == null) {
            onChapterPress(chapter)
            return
        }
        chapterList.value.let {
            if (it is State.Success) {
                val chapterRange = findChapterRange(chapter, previousSelectedChapter!!, it.value)

                val newList = selectedChapters.value.toMutableList().apply {
                    addAll(chapterRange)
                }
                selectedChapters.value = newList.distinctBy { it.id }
            }
        }
    }

    fun findChapterRange(
        currentChapter: DownloadChapter,
        previousChapter: DownloadChapter,
        listOfChapters: List<DownloadChapter>
    ): List<DownloadChapter> {
        val currentChapterIndex =
            listOfChapters.indexOfFirst { it.id == currentChapter.id }
        val previousChapterIndex =
            listOfChapters.indexOfFirst { it.id == previousChapter.id }

        return if (currentChapterIndex < previousChapterIndex) {
            listOfChapters.subList(currentChapterIndex, previousChapterIndex)
        } else if (currentChapterIndex > previousChapterIndex) {
            listOfChapters.subList(previousChapterIndex + 1, currentChapterIndex + 1)
        } else {
            listOf(currentChapter)
        }
    }

    fun selectAllChapters() {
        chapterList.value.let {
            if (it is State.Success) {
                val newList = selectedChapters.value.toMutableList().apply {
                    addAll(it.value)
                }
                selectedChapters.value = newList.distinctBy { it.id }
            }
        }
    }

    fun unselectAllChapters() {
        selectedChapters.value = emptyList()
        previousSelectedChapter = null
    }

    fun downloadChapters() {
        applicationScope.launch {
            downloadRepository.downloadChapters(selectedChapters.value.mapToDb())
        }
    }
}