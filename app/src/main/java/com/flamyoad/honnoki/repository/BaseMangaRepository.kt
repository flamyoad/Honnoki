package com.flamyoad.honnoki.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.*
import kotlinx.coroutines.flow.Flow

abstract class BaseMangaRepository(val db: AppDatabase) {
    abstract fun getRecentManga(): Flow<PagingData<Manga>>
    abstract fun getTrendingManga(): Flow<PagingData<Manga>>
    abstract fun getSourceType(): Source
    abstract suspend fun getMangaOverview(urlPath: String): State<MangaOverview>
    abstract suspend fun getChapterList(urlPath: String): State<List<Chapter>>

    companion object {
        @ExperimentalPagingApi
        fun get(source: Source, db: AppDatabase): BaseMangaRepository {
            return when (source) {
                Source.MANGAKALOT -> MangakalotRepository(db)
                Source.SENMANGA -> SenMangaRepository(db)
            }
        }
    }
}