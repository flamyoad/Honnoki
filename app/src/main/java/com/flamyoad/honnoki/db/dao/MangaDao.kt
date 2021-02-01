package com.flamyoad.honnoki.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.flamyoad.honnoki.db.typeconverters.SourceConverter
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.Source

@Dao
interface MangaDao {

    @Query("""
        SELECT * FROM manga 
        WHERE source = :source 
        ORDER BY id
        """)
    fun getFrom(source: Source): PagingSource<Int, Manga>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(mangas: List<Manga>)

    @Query("DELETE FROM manga WHERE source = :source")
    fun deleteFrom(source: Source): Int
}