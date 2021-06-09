package com.flamyoad.honnoki.data.db.dao

import androidx.room.*
import com.flamyoad.honnoki.data.model.Bookmark
import com.flamyoad.honnoki.data.model.BookmarkWithOverview
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Insert
    fun insert(bookmarks: List<Bookmark>)

    @Update
    fun update(bookmarks: List<Bookmark>)

    @Delete
    fun delete(bookmarks: List<Bookmark>)

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