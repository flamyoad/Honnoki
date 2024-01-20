package com.flamyoad.honnoki.source

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.MangadexApi
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.DynamicGenre
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.paging.LookupResultMediator
import com.flamyoad.honnoki.paging.MangaMediator
import com.flamyoad.honnoki.paging.SimpleSearchResultMediator
import com.flamyoad.honnoki.source.model.TabType
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class MangadexSource(db: AppDatabase, context: Context, private val api: MangadexApi) :
    BaseSource(db, context) {

    override fun getSourceType(): Source = Source.MANGADEX

    override fun getAvailableTabs(): List<TabType> {
        return listOf(TabType.MOST_RECENT, TabType.TRENDING)
    }

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, getSourceType(), MangaType.RECENTLY),
            pagingSourceFactory = { db.mangaDao().getFrom(getSourceType(), MangaType.RECENTLY) }
        ).flow
    }

    override fun getTrendingManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, getSourceType(), MangaType.TRENDING),
            pagingSourceFactory = { db.mangaDao().getFrom(getSourceType(), MangaType.TRENDING) }
        ).flow
    }

    override fun getSimpleSearch(query: String): Flow<PagingData<SearchResult>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = SimpleSearchResultMediator(
                api,
                db,
                query,
                GenreConstants.ALL
            ),
            pagingSourceFactory = { db.searchResultDao().getAll() }
        ).flow
    }

    override suspend fun getMangaOverview(urlPath: String): State<MangaOverview> {
        return api.searchForMangaOverview(urlPath)
    }

    override suspend fun getAuthors(urlPath: String): State<List<Author>> {
        return api.searchForAuthors(urlPath)
    }

    override suspend fun getGenres(urlPath: String): State<List<Genre>> {
        return api.searchForGenres(urlPath)
    }

    override suspend fun getChapterList(urlPath: String): State<List<Chapter>> {
        return api.searchForChapterList(urlPath)
    }

    override suspend fun getImages(urlPath: String): State<List<Page>> {
        return api.searchForImageList(urlPath)
    }

    override fun getMangaByAuthors(params: String): Flow<PagingData<LookupResult>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = LookupResultMediator(
                api,
                db,
                params,
                LookupType.AUTHOR
            ),
            pagingSourceFactory = { db.lookupDao().getAll() }
        ).flow
    }

    override fun getMangaByGenres(params: String): Flow<PagingData<LookupResult>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = LookupResultMediator(
                api,
                db,
                params,
                LookupType.GENRE
            ),
            pagingSourceFactory = { db.lookupDao().getAll() }
        ).flow
    }

    override suspend fun getDynamicGenres(): State<List<DynamicGenre>> {
        return api.getAvailableGenres()
    }

    companion object {
        const val PAGINATION_SIZE = 50
    }
}