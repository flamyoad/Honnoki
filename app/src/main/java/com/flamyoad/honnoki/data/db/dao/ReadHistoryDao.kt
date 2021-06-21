package com.flamyoad.honnoki.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.flamyoad.honnoki.data.entities.ReadHistory

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
        ORDER BY lastReadDateTime DESC
    """
    )
    fun getAll(): PagingSource<Int, ReadHistory>

    @Query(
        """
        UPDATE manga_overview
        SET lastReadChapterId = -1, lastReadDateTime = "", lastReadPageNumber = -1
        WHERE id = :overviewId
    """
    )
    fun removeReadHistory(overviewId: Long)
}