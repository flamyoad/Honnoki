package com.flamyoad.honnoki.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.honnoki.data.entities.LookupResult

@Dao
interface LookupDao {
    @Query("SELECT * FROM lookup_result")
    fun getAll(): PagingSource<Int, LookupResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(mangas: List<LookupResult>)

    @Query("DELETE FROM lookup_result")
    fun deleteAll(): Int
}