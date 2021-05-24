package com.flamyoad.honnoki.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.MangaType
import com.flamyoad.honnoki.model.Source

@Dao
interface MangaDao {

    @Query("""
        SELECT * FROM manga
        WHERE source = :source AND type = :type
        """)
    fun getFrom(source: Source, type: MangaType): PagingSource<Int, Manga>

    @Query("SELECT * FROM manga")
    fun getAll(): List<Manga>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(mangas: List<Manga>)

    @Query("DELETE FROM manga WHERE source = :source AND type = :type")
    fun deleteFrom(source: Source, type: MangaType): Int
}