package com.flamyoad.honnoki.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.Bookmark
import com.flamyoad.honnoki.data.model.BookmarkGroup
import com.flamyoad.honnoki.repository.BookmarkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MoveBookmarkDialogViewModel(
    db: AppDatabase,
    private val bookmarkRepo: BookmarkRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    private val selectedBookmarkGroup = MutableStateFlow(BookmarkGroup.empty())

    val bookmarkGroups = db.bookmarkGroupDao().getAll()
        .combine(selectedBookmarkGroup) { groupList, selected ->
            groupList.map { it.copy(isSelected = (it == selected)) }
        }
        .asLiveData()

    fun tickBookmarkGroup(group: BookmarkGroup) {
        selectedBookmarkGroup.value = (group)
    }

    fun moveBookmarks(bookmarkIds: List<Long>) {
        val bookmarkGroup = selectedBookmarkGroup.value
        applicationScope.launch(Dispatchers.IO) {
            bookmarkRepo.moveBookmarksToGroup(bookmarkIds, bookmarkGroup)
        }
    }
}