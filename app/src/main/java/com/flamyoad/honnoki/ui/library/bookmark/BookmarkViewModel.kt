package com.flamyoad.honnoki.ui.library.bookmark

import android.app.Application
import androidx.lifecycle.*
import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.BookmarkGroup
import com.flamyoad.honnoki.data.model.BookmarkGroupWithInfo
import com.flamyoad.honnoki.data.model.BookmarkWithOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkViewModel(
    private val db: AppDatabase
) : ViewModel() {

    private val bookmarkGroupDao = db.bookmarkGroupDao()

    private val selectedBookmarkGroupId = MutableStateFlow(-1L)

    val bookmarkGroupsWithInfo: LiveData<List<BookmarkGroupWithInfo>> =
        db.bookmarkGroupWithInfoDao()
            .getAll()
            .flatMapLatest { flowOf(attachCoverImagesToGroups(it)) }
            .asLiveData()

    var bookmarkGroupName: String = ""
        private set

    val selectedBookmarkGroup = selectedBookmarkGroupId
        .flatMapLatest {
            if (it == -1L) return@flatMapLatest flowOf(BookmarkGroup.empty())
            return@flatMapLatest db.bookmarkGroupDao().getById(it)
        }

    val bookmarkItems: LiveData<List<BookmarkWithOverview>> =
        selectedBookmarkGroup
            .flatMapLatest {
                val groupId = it?.id
                if (groupId != null)
                    return@flatMapLatest db.bookmarkDao().getAllWithOverviewFrom(groupId)
                else
                    return@flatMapLatest flowOf(emptyList())
            }
            .asLiveData()

    init {
        initializeBookmarkGroups()
    }

    private fun initializeBookmarkGroups() {
        // move init part to mainactivity!! or Application
        viewModelScope.launch(Dispatchers.IO) {
            db.withTransaction {
                if (bookmarkGroupDao.getAllBlocking().isEmpty()) {
                    bookmarkGroupDao.insert(BookmarkGroup(name = "All"))
                }
                selectedBookmarkGroupId.value = bookmarkGroupDao.getFirstItemId()
            }
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

    fun getSelectedBookmarkGroupId(): Long {
        return requireNotNull(selectedBookmarkGroupId.value)
    }

    fun selectBookmarkGroup(bookmarkGroup: BookmarkGroup) {
        selectedBookmarkGroupId.value = bookmarkGroup.id ?: -1
    }
}