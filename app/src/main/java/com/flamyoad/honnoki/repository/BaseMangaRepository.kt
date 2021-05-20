package com.flamyoad.honnoki.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.*
import kotlinx.coroutines.flow.Flow

abstract class BaseMangaRepository(val db: AppDatabase, val context: Context) {
    abstract fun getRecentManga(): Flow<PagingData<Manga>>
    abstract fun getTrendingManga(): Flow<PagingData<Manga>>
    abstract fun getSourceType(): Source
    abstract suspend fun getMangaOverview(urlPath: String): State<MangaOverview>
    abstract suspend fun getChapterList(urlPath: String): State<List<Chapter>>

    open suspend fun getImages(urlPath: String): State<List<Page>> {
        return State.Error()
    }

    companion object {
        @ExperimentalPagingApi
        fun get(source: Source, db: AppDatabase, context: Context): BaseMangaRepository {
            return when (source) {
                Source.MANGAKALOT -> MangakalotRepository(db, context)
                Source.SENMANGA -> SenMangaRepository(db, context)
            }
        }
    }
}