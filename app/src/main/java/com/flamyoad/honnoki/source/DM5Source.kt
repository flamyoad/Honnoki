package com.flamyoad.honnoki.source

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.DM5Api
import com.flamyoad.honnoki.api.MangakalotApi
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.*
import com.flamyoad.honnoki.paging.MangaMediator
import com.flamyoad.honnoki.paging.SimpleSearchResultMediator
import kotlinx.coroutines.flow.Flow

@ExperimentalPagingApi
class DM5Source(db: AppDatabase, context: Context, private val api: DM5Api) :
    BaseSource(db, context) {

    override fun getSourceType(): Source {
        return Source.DM5
    }

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = NORMAL_PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, getSourceType(), MangaType.RECENTLY),
            pagingSourceFactory = { db.mangaDao().getFrom(getSourceType(), MangaType.RECENTLY) }
        ).flow
    }

    override fun getTrendingManga(): Flow<PagingData<Manga>> {
        return super.getTrendingManga()
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

    override fun getSimpleSearch(query: String): Flow<PagingData<SearchResult>> {
        return Pager(
            config = PagingConfig(pageSize = NORMAL_PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = SimpleSearchResultMediator(
                api,
                db,
                query,
                GenreConstants.ALL,
                getSourceType()
            ),
            pagingSourceFactory = { db.searchResultDao().getAll() }
        ).flow
    }

    companion object {
        private const val NORMAL_PAGINATION_SIZE = 30
    }
}