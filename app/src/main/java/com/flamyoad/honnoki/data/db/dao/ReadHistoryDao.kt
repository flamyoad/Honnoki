package com.flamyoad.honnoki.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.flamyoad.honnoki.data.model.ReadHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadHistoryDao {

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT *, 
        overview.id AS overviewId,
        overview.link AS overviewLink
        FROM manga_overview AS overview
        INNER JOIN chapters ON chapters.id == lastReadChapterId
    """
    )
    fun getAll(): Flow<List<ReadHistory>>

    @Query(
        """
        UPDATE manga_overview
        SET lastReadChapterId = -1, lastReadTime = "", lastReadPageNumber = -1
        WHERE id = :overviewId
    """
    )
    fun removeReadHistory(overviewId: Long)
}