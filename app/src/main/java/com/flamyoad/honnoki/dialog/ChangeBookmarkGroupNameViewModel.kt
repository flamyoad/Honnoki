package com.flamyoad.honnoki.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.MyApplication
import com.flamyoad.honnoki.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class ChangeBookmarkGroupNameViewModel(application: Application, private val db: AppDatabase) : AndroidViewModel(application) {
    private val applicationScope = (application as MyApplication).applicationScope

    private val nameInputByUser = MutableStateFlow("")

    private val bookmarkGroupId = MutableStateFlow(-1L)

    val bookmarkGroup = bookmarkGroupId
        .filter { it != -1L }
        .flatMapLatest { db.bookmarkGroupDao().getById(it) }
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
        applicationScope.launch(Dispatchers.IO) {
            val oldBookmarkGroup = bookmarkGroup.value ?: return@launch

            val newBookmarkGroup = oldBookmarkGroup.copy(name = nameInputByUser.value)

            db.bookmarkGroupDao().update(newBookmarkGroup)
        }
    }
}