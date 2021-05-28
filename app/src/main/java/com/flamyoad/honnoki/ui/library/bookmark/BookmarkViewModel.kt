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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getInstance(application.applicationContext)

    private val bookmarkGroupDao = db.bookmarkGroupDao()

    private val selectedBookmarkGroup = MutableStateFlow(BookmarkGroup.empty())
    fun selectedBookmarkGroup(): LiveData<BookmarkGroup> = selectedBookmarkGroup
        .asLiveData()

    val bookmarkGroupsWithCoverImages: LiveData<List<BookmarkGroupWithCoverImages>> =
        db.bookmarkGroupDao().getAllWithCoverImages()

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
        viewModelScope.launch(Dispatchers.IO) {
            db.withTransaction {
                if (bookmarkGroupDao.getAllBlocking().isEmpty()) {
                    bookmarkGroupDao.insert(BookmarkGroup(name = "All"))
                }
                selectedBookmarkGroup.value  = bookmarkGroupDao.getFirst()
            }
        }
    }

    fun selectBookmarkGroup(bookmarkGroup: BookmarkGroup) {
        selectedBookmarkGroup.value = bookmarkGroup
    }
}