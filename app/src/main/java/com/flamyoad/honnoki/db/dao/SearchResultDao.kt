package com.flamyoad.honnoki.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.honnoki.model.SearchResult
import com.flamyoad.honnoki.model.Source

@Dao
interface SearchResultDao {
    @Query("SELECT * FROM searched_result")
    fun getAll(): PagingSource<Int, SearchResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(mangas: List<SearchResult>)

    @Query("DELETE FROM searched_result")
    fun deleteAll(): Int
}