package com.flamyoad.honnoki.source

import android.content.Context
import androidx.paging.PagingData
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.DynamicGenre
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.source.model.TabType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

abstract class BaseSource(val db: AppDatabase, val context: Context) {
    abstract fun getSourceType(): Source
    abstract fun getAvailableTabs(): List<TabType>

    open fun getRecentManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    // Hot
    open fun getTrendingManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    open fun getTopManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    open fun getLatestManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    open fun getNewManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    open fun getSimpleSearch(query: String): Flow<PagingData<SearchResult>> {
        return emptyFlow()
    }

    open fun getSimpleSearchWithGenre(query: String, genre: GenreConstants): Flow<PagingData<SearchResult>> {
        return emptyFlow()
    }

    open fun getMangaByAuthors(params: String): Flow<PagingData<LookupResult>> {
        return emptyFlow()
    }

    open fun getMangaByGenres(params: String): Flow<PagingData<LookupResult>> {
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

    open suspend fun getDynamicGenres(): State<List<DynamicGenre>> {
        return State.Error()
    }
}