package com.flamyoad.honnoki.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

abstract class BaseMangaRepository(val db: AppDatabase, val context: Context) {
    abstract fun getSourceType(): Source

    open fun getRecentManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    open fun getTrendingManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    open fun getTopManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    open fun getSimpleSearch(query: String): Flow<PagingData<SearchResult>> {
        return emptyFlow()
    }

    open suspend fun getMangaOverview(urlPath: String): State<MangaOverview> {
        return State.Error()
    }

    open suspend fun getChapterList(urlPath: String): State<List<Chapter>> {
        return State.Error()
    }

    open suspend fun getAuthors(urlPath: String): State<List<Author>> {
        return State.Error()
    }

    open suspend fun getGenres(urlPath: String): State<List<Genre>> {
        return State.Error()
    }

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