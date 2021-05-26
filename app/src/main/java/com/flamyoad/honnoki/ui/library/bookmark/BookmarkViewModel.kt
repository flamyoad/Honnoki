package com.flamyoad.honnoki.ui.library.bookmark

import android.app.Application
import androidx.lifecycle.*
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Bookmark
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getInstance(application.applicationContext)

    private val bookmarkGroupDao = db.bookmarkGroupDao()

    private val bookmarkDao = db.bookmarkDao()

    private val selectedBookmarkGroup = MutableStateFlow(BookmarkGroup.empty())

    val bookmarkGroupsWithCoverImages: LiveData<List<BookmarkGroupWithCoverImages>>

    val bookmarkItems: LiveData<List<Bookmark>>

    init {
        populateInitialData()

        bookmarkGroupsWithCoverImages = db.bookmarkGroupDao().getAllWithCoverImages()

        bookmarkItems = selectedBookmarkGroup
            .filter { it -> it.id != null }
            .flatMapLatest { return@flatMapLatest bookmarkDao.getAllFrom(requireNotNull(it.id)) }
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