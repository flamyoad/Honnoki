package com.flamyoad.honnoki.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.MyApplication
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.BookmarkGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class AddBookmarkGroupDialogViewModel(
    application: Application, private val db: AppDatabase
) :
    AndroidViewModel(application) {

    private val applicationScope = (application as MyApplication).applicationScope

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
        applicationScope.launch(Dispatchers.IO) {
            val name = nameInputByUser.value
            db.bookmarkGroupDao().insert(BookmarkGroup(name = name))
        }
    }
}