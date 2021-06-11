package com.flamyoad.honnoki.repository

import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.ReadHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ReadHistoryRepository(private val db: AppDatabase) {

    private val readHistoryDao get() = db.readHistoryDao()

    fun getAllHistories(): Flow<List<ReadHistory>> {
        return readHistoryDao.getAll()
    }

    suspend fun removeHistory(history: ReadHistory) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                readHistoryDao.removeReadHistory(history.overviewId)
            }
        }
    }
}