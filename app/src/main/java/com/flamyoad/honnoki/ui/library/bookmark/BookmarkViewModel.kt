package com.flamyoad.honnoki.ui.library.bookmark

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages
import com.flamyoad.honnoki.model.BookmarkWithOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getInstance(application.applicationContext)

    private val bookmarkGroupDao = db.bookmarkGroupDao()

    private val selectedBookmarkGroupId = MutableStateFlow(-1L)

    val bookmarkGroupsWithCoverImages: LiveData<List<BookmarkGroupWithCoverImages>> =
        db.bookmarkGroupWithCoverImageDao()
            .getAll()
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
                val groupId = it.id
                if (groupId != null)
                    return@flatMapLatest db.bookmarkDao().getAllWithOverviewFrom(groupId)
                else
                    return@flatMapLatest emptyFlow()
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
                selectedBookmarkGroupId.value  = bookmarkGroupDao.getFirstItemId()
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