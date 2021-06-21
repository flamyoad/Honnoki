package com.flamyoad.honnoki.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.honnoki.data.entities.Author
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(authorList: List<Author>)

    @Query("SELECT * FROM authors WHERE mangaOverviewId = :mangaOverviewId")
    fun getByOverviewId(mangaOverviewId: Long): Flow<List<Author>>

    @Query("SELECT * FROM authors WHERE mangaOverviewId = :mangaOverviewId")
    suspend fun getByOverviewIdBlocking(mangaOverviewId: Long): List<Author>
}