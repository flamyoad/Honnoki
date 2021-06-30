package com.flamyoad.honnoki.repository

import androidx.paging.*
import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.ReadHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReadHistoryRepository(private val db: AppDatabase) {

    private val readHistoryDao get() = db.readHistoryDao()

    fun getAllHistories(): Flow<PagingData<ReadHistory>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { readHistoryDao.getAll() }
        ).flow

    suspend fun removeHistory(history: ReadHistory) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                val overviewId = requireNotNull(history.overview.id)
                readHistoryDao.removeReadHistory(overviewId)
            }
        }
    }
}