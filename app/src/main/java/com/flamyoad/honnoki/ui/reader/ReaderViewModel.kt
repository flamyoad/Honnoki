package com.flamyoad.honnoki.ui.reader

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.room.withTransaction
import com.flamyoad.honnoki.common.BaseViewModel
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.data.entities.Page
import com.flamyoad.honnoki.data.preference.ReaderPreference
import com.flamyoad.honnoki.repository.ChapterRepository
import com.flamyoad.honnoki.repository.OverviewRepository
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.ui.reader.model.LoadType
import com.flamyoad.honnoki.ui.reader.model.ReaderOrientation
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import com.flamyoad.honnoki.ui.reader.model.ReaderViewMode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap

@ExperimentalPagingApi
class ReaderViewModel(
    private val db: AppDatabase,
    private val chapterRepo: ChapterRepository,
    private val overviewRepo: OverviewRepository,
    private val applicationScope: CoroutineScope,
    private val baseSource: BaseSource,
    private val readerPrefs: ReaderPreference
) : BaseViewModel() {

    val source get() = baseSource.getSourceType()

    val mangadexQualityMode get() = runBlocking { readerPrefs.mangadexQualityMode.first() }

    val extraSpaceAtBottomIndicator get() = runBlocking { readerPrefs.extraSpaceAtBottomIndicator.first() }

    val viewMode = readerPrefs.viewMode.asLiveData()

    val orientation = readerPrefs.orientation.asLiveData()

    val overviewId get() = mangaOverviewId.value

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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val currentPageIndicator =
        currentPageNumber.combine(totalPageNumber) { current, total ->
            "$current / $total"
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    private val pageNumberScrolledBySeekbar = MutableSharedFlow<Int>(
        replay = 0,
        extraBufferCapacity = 1
    )

    fun pageNumberScrolledBySeekbar() =
        pageNumberScrolledBySeekbar.asSharedFlow()

    private val pageList = MutableStateFlow<List<ReaderPage>>(emptyList())
    fun pageList() = pageList.asStateFlow()

    private val isLoadingNextChapter = MutableStateFlow(false)
    fun isLoadingNextChapter() = isLoadingNextChapter.asStateFlow()

    private val failedToLoadNextChapter = MutableStateFlow(false)
    fun failedToLoadNextChapter() = failedToLoadNextChapter.asStateFlow()

    private val shouldShowAds =
        readerPrefs.shouldShowAds(baseSource.getSourceType())

    private var loadPrevChapterJob: Job? = null
    private var loadNextChapterJob: Job? = null
    private var saveLastReadPageJob: Job? = null

    private val loadCompletionStatusByChapterId =
        ConcurrentHashMap<Long, Boolean>()

    var selectedLanguage: String? = null

    var currentScrollPosition: Int = -1

    fun fetchChapterImages(chapterId: Long, loadType: LoadType): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val chapter = db.chapterDao().get(chapterId)
                ?: throw IllegalArgumentException("Chapter id is null")

            selectedLanguage = chapter.translatedLanguage

            // Skip loading this chapter if it has been loaded before
            if (loadCompletionStatusByChapterId[chapterId] == true) {
                return@launch
            }

            if (loadType == LoadType.NEXT) {
                isLoadingNextChapter.value = true
            }

            when (val result = baseSource.getImages(chapter.link)) {
                is State.Success -> processChapterImages(
                    chapter,
                    result.value,
                    loadType
                )
                is State.Error -> failedToLoadNextChapter.value = true
            }

            // Hides the loading indicator regardless success or failed to load next chapter
            isLoadingNextChapter.value = false
        }
    }

    private suspend fun processChapterImages(
        chapter: Chapter,
        pages: List<Page>,
        loadType: LoadType
    ) {
        val chapterId =
            chapter.id ?: throw IllegalArgumentException("Chapter id is null")

        val pagesFromNetwork = pages.map {
            it.copy(chapterId = chapterId)
        }

        db.withTransaction {
            db.pageDao().insertAll(pagesFromNetwork)
        }

        val pagesFromDb = db.pageDao().getPagesWithChapterInfo(chapterId)

        val existingList = pageList.value.toMutableList()

        when (loadType) {
            LoadType.INITIAL -> {
                existingList.clear()
                existingList.addAll(pagesFromDb.map { ReaderPage.Value(it) })
                if (shouldShowAds) {
                    existingList.add(ReaderPage.Ads(chapterId))
                }
            }
            LoadType.PREV -> {
                val list =
                    pagesFromDb.map { ReaderPage.Value(it) } as MutableList<ReaderPage>
                existingList.addAll(0, list)
                if (shouldShowAds) {
                    list.add(ReaderPage.Ads(chapterId))
                }
            }
            LoadType.NEXT -> {
                val list = pagesFromDb.map { ReaderPage.Value(it) }
                existingList.addAll(list)
                if (shouldShowAds) {
                    existingList.add(ReaderPage.Ads(chapterId))
                }
            }
        }

        // Submits pages from the newly loaded chapter to adapter
        pageList.value = existingList

        // Marks the chapter as completed to prevent duplicate loading
        loadCompletionStatusByChapterId.put(chapter.id, true)
    }

    fun restoreLastReadChapter(overviewId: Long, fallbackChapterId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val overview = db.mangaOverviewDao().getByIdBlocking(overviewId)
            overview?.lastReadChapterId?.let {
                if (it != -1L) {
                    fetchChapterImages(it, LoadType.INITIAL)
                } else {
                    fetchChapterImages(fallbackChapterId, LoadType.INITIAL)
                }
            }
        }
    }

    fun markChapterAsRead(chapter: Chapter) {
        if (chapter == Chapter.empty()) return
        applicationScope.launch(Dispatchers.IO) {
            chapterRepo.markChapterAsRead(chapter)
        }
    }

    fun saveLastReadChapter(chapter: Chapter) {
        val overviewId = mangaOverviewId.value
        applicationScope.launch(Dispatchers.IO) {
            overviewRepo.updateLastReadChapter(chapter, overviewId)
        }
    }

    fun saveLastReadPage(pageNumber: Int) {
        val overviewId = mangaOverviewId.value
        saveLastReadPageJob?.cancel()
        saveLastReadPageJob = applicationScope.launch(Dispatchers.IO) {
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

            val lang = selectedLanguage
            val prevChapter = if (lang != null) {
                db.chapterDao()
                    .getPreviousChapterFromLanguage(
                        overviewId,
                        currentChapterNumber,
                        lang
                    )
            } else {
                db.chapterDao()
                    .getPreviousChapter(overviewId, currentChapterNumber)
            }

            val chapterId = prevChapter?.id ?: return@launch

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

            val lang = selectedLanguage
            val nextChapter = if (lang != null) {
                db.chapterDao().getNextChapterFromLanguage(
                    overviewId,
                    currentChapterNumber,
                    lang
                )
            } else {
                db.chapterDao().getNextChapter(overviewId, currentChapterNumber)
            }

            val chapterId = nextChapter?.id ?: return@launch

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
        pageNumberScrolledBySeekbar.tryEmit(1)
    }

    fun goToLastPage() {
        val lastPage = totalPageNumber.value
        pageNumberScrolledBySeekbar.tryEmit(lastPage)
    }

    suspend fun getMangaOverview(overviewId: Long): MangaOverview? {
        return db.mangaOverviewDao().getByIdBlocking(overviewId)
    }

    fun getViewModeBlocking() = runBlocking { readerPrefs.viewMode.first() }

    fun getOrientationBlocking() =
        runBlocking { readerPrefs.orientation.first() }

    fun editViewMode(viewMode: ReaderViewMode) {
        applicationScope.launch(Dispatchers.IO) {
            readerPrefs.editReaderViewMode(viewMode)
        }
    }

    fun editOrientation(orientation: ReaderOrientation) {
        applicationScope.launch(Dispatchers.IO) {
            readerPrefs.editReaderOrientation(orientation)
        }
    }
}