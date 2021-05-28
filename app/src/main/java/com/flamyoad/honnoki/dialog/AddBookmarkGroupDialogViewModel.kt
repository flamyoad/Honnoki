package com.flamyoad.honnoki.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.BookmarkGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class AddBookmarkGroupDialogViewModel(application: Application) : AndroidViewModel(application) {

    private val db: AppDatabase = AppDatabase.getInstance(application)

    private val nameInputByUser = MutableStateFlow("")

    val nameAlreadyExists = nameInputByUser
        .flatMapLatest {
            if (it.isBlank())
                return@flatMapLatest flowOf(false)
            else
                return@flatMapLatest db.bookmarkGroupDao().existsByName(it)
        }
        .asLiveData()

    fun setInputName(name: String) {
        nameInputByUser.value = name
    }

    fun createNewGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            val name = nameInputByUser.value
            db.bookmarkGroupDao().insert(BookmarkGroup(name = name))
        }
    }
}