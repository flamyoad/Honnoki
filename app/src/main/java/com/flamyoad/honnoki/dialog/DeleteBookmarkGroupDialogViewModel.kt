package com.flamyoad.honnoki.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.MyApplication
import com.flamyoad.honnoki.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class DeleteBookmarkGroupDialogViewModel(application: Application, private val db: AppDatabase) :
    AndroidViewModel(application) {

    private val applicationScope = (application as MyApplication).applicationScope

    private val bookmarkGroupId = MutableStateFlow(-1L)

    val bookmarkGroup = bookmarkGroupId
        .flatMapLatest {
            if (it == -1L) return@flatMapLatest emptyFlow()
            return@flatMapLatest db.bookmarkGroupDao().getById(it)
        }
        .asLiveData()

    fun setBookmarkGroupId(id: Long) {
        bookmarkGroupId.value = id
    }

    fun deleteGroup(id: Long) {
        applicationScope.launch(Dispatchers.IO) {
            db.bookmarkGroupDao().delete(id)
        }
    }
}