package com.flamyoad.honnoki.ui.library.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.ReadHistory
import com.flamyoad.honnoki.repository.ReadHistoryRepository
import com.flamyoad.honnoki.ui.library.history.model.ViewReadHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class ReadHistoryViewModel(
    private val db: AppDatabase,
    private val historyRepo: ReadHistoryRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    val readHistory =
        historyRepo.getAllHistories().map { pagingData: PagingData<ReadHistory> ->
            pagingData.map {
                ViewReadHistory.Item(it)
            }.insertSeparators { before, after ->
                return@insertSeparators if (after == null) {
                    null
                } else if (before == null || before.lastReadDate != after.lastReadDate) {
                    ViewReadHistory.Header(after.lastReadDate)
                } else {
                    null
                }
            }
        }.cachedIn(viewModelScope)

    fun removeHistory(history: ReadHistory) {
//        applicationScope.launch {
//            bookmarkRepo.removeHistory(history)
//        }
    }
}
