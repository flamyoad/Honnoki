package com.flamyoad.honnoki.ui.library.history

import androidx.lifecycle.ViewModel
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.ReadHistory
import com.flamyoad.honnoki.data.model.State
import com.flamyoad.honnoki.repository.ReadHistoryRepository
import com.flamyoad.honnoki.ui.library.history.model.ViewReadHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReadHistoryViewModel(
    private val db: AppDatabase,
    private val bookmarkRepo: ReadHistoryRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    val readHistory: Flow<State<List<ViewReadHistory>>> = bookmarkRepo.getAllHistories()
        .flowOn(Dispatchers.IO)
        .onStart { State.Loading }
        .mapLatest { State.Success(groupHistoriesByDate(it)) }
        .flowOn(Dispatchers.Default)

    fun removeHistory(history   : ReadHistory) {
        applicationScope.launch {
            bookmarkRepo.removeHistory(history)
        }
    }

    private fun groupHistoriesByDate(histories: List<ReadHistory>): List<ViewReadHistory> {
        val groups = histories
            .sortedByDescending { it.lastReadTime }
            .groupBy { it.lastReadTime.toLocalDate() }

        return groups.flatMap {
            val list = mutableListOf<ViewReadHistory>()

            val header = ViewReadHistory.Header(it.key)
            list.add(header)

            val items = it.value.map { ViewReadHistory.Item(it) }
            list.addAll(items)

            return@flatMap list
        }
    }
}