package com.flamyoad.honnoki.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.honnoki.model.Bookmark
import com.flamyoad.honnoki.model.BookmarkWithOverview
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Insert
    fun insert(bookmark: List<Bookmark>)

    @Query("SELECT * FROM bookmark WHERE bookmarkGroupId = :bookmarkGroupId")
    fun getAllFrom(bookmarkGroupId: Long): Flow<List<Bookmark>>

    @Transaction
    @Query("SELECT * FROM bookmark WHERE bookmarkGroupId = :bookmarkGroupId")
    fun getAllWithOverviewFrom(bookmarkGroupId: Long): Flow<List<BookmarkWithOverview>>

    @Transaction
    @Query("SELECT * FROM bookmark WHERE bookmarkGroupId = :bookmarkGroupId")
    fun getAllWithOverviewFromBlocking(bookmarkGroupId: Long): List<BookmarkWithOverview>

    @Query("DELETE FROM bookmark WHERE mangaOverviewId = :overviewId")
    suspend fun deleteAllFrom(overviewId: Long): Int
}