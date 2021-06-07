package com.flamyoad.honnoki.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.flamyoad.honnoki.MyApplication
import com.flamyoad.honnoki.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ChangeBookmarkGroupNameViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getInstance(application)

    private val applicationScope = (application as MyApplication).applicationScope

    private val nameInputByUser = MutableStateFlow("")

    private val bookmarkGroupId = MutableStateFlow(-1L)

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
        applicationScope.launch(Dispatchers.IO) {
            val oldBookmarkGroup = requireNotNull(bookmarkGroup.value)

            val newBookmarkGroup = oldBookmarkGroup.copy(name = nameInputByUser.value)

            db.bookmarkGroupDao().update(newBookmarkGroup)
        }
    }
}