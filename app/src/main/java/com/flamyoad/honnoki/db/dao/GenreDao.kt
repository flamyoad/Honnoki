package com.flamyoad.honnoki.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.honnoki.model.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(genreList: List<Genre>)

    @Query("SELECT * FROM genres WHERE mangaOverviewId = :mangaOverviewId")
    fun getByOverviewId(mangaOverviewId: Long): Flow<List<Genre>>

    @Query("SELECT * FROM genres WHERE mangaOverviewId = :mangaOverviewId")
    suspend fun getByOverviewIdBlocking(mangaOverviewId: Long): List<Genre>
}