package com.flamyoad.honnoki.ui.library.bookmark

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Bookmark
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages
import com.flamyoad.honnoki.model.BookmarkWithOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getInstance(application.applicationContext)

    private val bookmarkGroupDao = db.bookmarkGroupDao()

    private val selectedBookmarkGroup = MutableStateFlow(BookmarkGroup.empty())
    fun selectedBookmarkGroup(): LiveData<BookmarkGroup> = selectedBookmarkGroup.asLiveData()

    val bookmarkGroupsWithCoverImages: LiveData<List<BookmarkGroupWithCoverImages>>

    val bookmarkItems: LiveData<List<BookmarkWithOverview>>

    init {
        populateInitialData()

        bookmarkGroupsWithCoverImages = db.bookmarkGroupDao().getAllWithCoverImages()

        bookmarkItems = selectedBookmarkGroup
            .flatMapLatest {
                val groupId = it.id
                if (groupId != null)
                    return@flatMapLatest db.bookmarkDao().getAllWithOverviewFrom(groupId)
                else
                    return@flatMapLatest emptyFlow()
            }
            .asLiveData()
    }

    private fun populateInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (bookmarkGroupDao.getAll().isEmpty()) {
                bookmarkGroupDao.insert(BookmarkGroup(name = "All"))
            }
        }
    }
}