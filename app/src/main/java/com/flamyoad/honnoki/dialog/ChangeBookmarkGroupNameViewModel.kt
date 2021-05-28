package com.flamyoad.honnoki.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.BookmarkGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ChangeBookmarkGroupNameViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getInstance(application)

    private val bookmarkGroupId = MutableStateFlow(-1L)

    private val nameInputByUser = MutableStateFlow("")

    val bookmarkGroup = bookmarkGroupId
        .flatMapLatest {
            if (it == -1L) return@flatMapLatest emptyFlow()
            return@flatMapLatest db.bookmarkGroupDao().getById(it)
        }
        .asLiveData()

    val nameAlreadyExists = nameInputByUser
        .flatMapLatest {
            if (it.isBlank())
                return@flatMapLatest flowOf(false)
            else
                return@flatMapLatest db.bookmarkGroupDao().existsByName(it)
        }
        .asLiveData()

    fun setBookmarkGroupId(id: Long) {
        bookmarkGroupId.value = id
    }

    fun setInputName(name: String) {
        nameInputByUser.value = name
    }

    fun changeGroupName() {
        viewModelScope.launch(Dispatchers.IO) {
            val oldBookmarkGroup = requireNotNull(bookmarkGroup.value)

            val newBookmarkGroup = oldBookmarkGroup.copy(name = nameInputByUser.value)

            db.bookmarkGroupDao().update(newBookmarkGroup)
        }
    }
}