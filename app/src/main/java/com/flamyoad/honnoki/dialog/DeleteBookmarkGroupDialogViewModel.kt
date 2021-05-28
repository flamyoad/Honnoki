package com.flamyoad.honnoki.dialog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteBookmarkGroupDialogViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getInstance(application)

    fun deleteGroup(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.bookmarkGroupDao().delete(id)
        }
    }
}