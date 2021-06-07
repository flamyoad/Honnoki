package com.flamyoad.honnoki.dialog

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.room.withTransaction
import com.flamyoad.honnoki.MyApplication
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.Bookmark
import com.flamyoad.honnoki.data.model.BookmarkGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class BookmarkDialogViewModel(application: Application, private val db: AppDatabase) : AndroidViewModel(application) {
    private val applicationScope = (application as MyApplication).applicationScope

    private val bookmarkGroupDao = db.bookmarkGroupDao()

    private val bookmarkGroups = MutableLiveData<List<BookmarkGroup>>()
    fun bookmarkGroups(): LiveData<List<BookmarkGroup>> = bookmarkGroups

    private var currentMangaOverviewId: Long = -1

    fun getBookmarkGroups(overviewId: Long) {
        currentMangaOverviewId = overviewId

        viewModelScope.launch(Dispatchers.IO) {
            val groups = bookmarkGroupDao.getAllBlocking().map {
                if (bookmarkGroupDao.hasBookmarkedItems(requireNotNull(it.id), overviewId)) {
                    it.copy(isSelected = true)
                } else {
                    it.copy(isSelected = false)
                }
            }
            bookmarkGroups.postValue(groups)
        }
    }

    fun toggleBookmarkGroup(bookmarkGroup: BookmarkGroup) {
        val oldList = bookmarkGroups.value ?: return
        val newList = oldList.map {
            if (it.id == bookmarkGroup.id) {
                bookmarkGroup.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }
        bookmarkGroups.value = newList
    }

    /**
     *  This function first deletes all bookmarks rows that have the same [mangaOverviewId]
     *  Then, inserts new bookmarks based on the tick status from dialog
     *  This operation is safe because it runs in transaction
     */
    fun saveBookmarkGroup() {
        applicationScope.launch(Dispatchers.IO) {
            db.withTransaction {
                db.bookmarkDao().deleteAllFrom(currentMangaOverviewId)

                val currentBookmarkGroups = bookmarkGroups.value ?: emptyList()

                val newBookmarks = currentBookmarkGroups
                    .filter { it -> it.isSelected }
                    .map {
                    Bookmark(
                        bookmarkGroupId = requireNotNull(it.id),
                        mangaOverviewId = currentMangaOverviewId
                    )
                }
                db.bookmarkDao().insert(newBookmarks)
            }
        }
    }

    fun clearBookmarkGroup() {
        bookmarkGroups.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("vm", "vm clered")
    }
}