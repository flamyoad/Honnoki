package com.flamyoad.honnoki.ui.library.bookmark

import androidx.lifecycle.*
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Bookmark
import com.flamyoad.honnoki.data.entities.BookmarkGroup
import com.flamyoad.honnoki.data.entities.BookmarkGroupWithInfo
import com.flamyoad.honnoki.data.entities.BookmarkWithOverview
import com.flamyoad.honnoki.repository.BookmarkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkViewModel(
    private val db: AppDatabase,
    private val bookmarkRepo: BookmarkRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    private val bookmarkGroupDao get() = db.bookmarkGroupDao()

    private val selectedBookmarkGroupId = MutableStateFlow(-1L)

    val bookmarkGroupsWithInfo: LiveData<List<BookmarkGroupWithInfo>> =
        db.bookmarkGroupWithInfoDao()
            .getAll()
            .flatMapLatest { flowOf(attachCoverImagesToGroups(it)) }
            .asLiveData()

    val selectedBookmarkGroup = selectedBookmarkGroupId
        .onEach { flowOf(BookmarkGroup.empty()) }
        .flatMapLatest { db.bookmarkGroupDao().getById(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), BookmarkGroup.empty())

    val bookmarkGroupName get() = selectedBookmarkGroup.value?.name

    private val tickedItems = MutableStateFlow<List<Bookmark>>(emptyList())
    fun tickedItems() = tickedItems.asStateFlow()

    val bookmarkItems: LiveData<List<BookmarkWithOverview>> = selectedBookmarkGroup
        .onEach { flowOf(emptyList<BookmarkWithOverview>()) }
        .filter { it != BookmarkGroup.empty() }
        .flatMapLatest { db.bookmarkDao().getAllWithOverviewFrom(it?.id ?: -1L) }
        .combine(tickedItems) { fromDb, tickedBookmarks ->
            fromDb.map { it.copy(isSelected = it.bookmark in tickedBookmarks) }
        }
        .asLiveData()

    private val isSearching = MutableStateFlow(false)
    fun isSearching() = isSearching.asStateFlow()

    init {
        initializeBookmarkGroups()
    }

    private fun initializeBookmarkGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedBookmarkGroupId.value = bookmarkGroupDao.getFirstItemId() ?: -1L
        }
    }

    private suspend fun attachCoverImagesToGroups(groups: List<BookmarkGroupWithInfo>): List<BookmarkGroupWithInfo> {
        return withContext(Dispatchers.IO) {
            groups.map {
                val bookmarkGroupId = it.bookmarkGroup.id ?: -1
                val coverImages = db.bookmarkGroupDao().getCoverImagesFrom(bookmarkGroupId)
                return@map it.copy(coverImages = coverImages)
            }
        }
    }

    fun tickBookmark(item: BookmarkWithOverview) {
        val list = tickedItems.value.toMutableList()
        if (item.isSelected) {
            list.remove(item.bookmark)
        } else {
            list.add(item.bookmark)
        }
        tickedItems.value = list
    }

    fun clearTickedBookmarks() {
        tickedItems.value = emptyList()
    }

    fun deleteBookmarks() {
        applicationScope.launch(Dispatchers.IO) {
            db.bookmarkDao().delete(tickedItems.value)
        }
    }

    fun getSelectedBookmarkGroupId(): Long {
        return requireNotNull(selectedBookmarkGroupId.value)
    }

    fun selectBookmarkGroup(bookmarkGroup: BookmarkGroup) {
        selectedBookmarkGroupId.value = bookmarkGroup.id ?: -1
    }

    fun toggleSearch() {
        isSearching.value = !isSearching.value
    }
}