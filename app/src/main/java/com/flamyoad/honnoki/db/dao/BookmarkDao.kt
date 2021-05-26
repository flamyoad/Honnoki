package com.flamyoad.honnoki.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.flamyoad.honnoki.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkDao {

    @Query("SELECT * FROM bookmark WHERE bookmarkGroupId = :bookmarkGroupId")
    fun getAllFrom(bookmarkGroupId: Long): Flow<List<Bookmark>>
}