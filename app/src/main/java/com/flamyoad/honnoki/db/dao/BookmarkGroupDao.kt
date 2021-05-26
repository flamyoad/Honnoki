package com.flamyoad.honnoki.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages

@Dao
interface BookmarkGroupDao {
    @Transaction
    @Query("SELECT * FROM bookmark_group")
    fun getAllWithCoverImages(): LiveData<List<BookmarkGroupWithCoverImages>>

    @Query("SELECT * FROM bookmark_group")
    suspend fun getAll(): List<BookmarkGroup>

    @Insert
    suspend fun insert(bookmarkGroup: BookmarkGroup)
}