package com.flamyoad.honnoki.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.MangakalotApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.*
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.paging.MangaMediator
import com.flamyoad.honnoki.paging.SimpleSearchResultMediator
import com.flamyoad.honnoki.ui.search.model.SearchGenre
import com.flamyoad.honnoki.utils.GenreConstants
import kotlinx.coroutines.flow.Flow

@ExperimentalPagingApi
class MangakalotRepository(db: AppDatabase, context: Context) : BaseMangaRepository(db, context) {
    private val api: MangakalotApi = MangakalotApi(MangakalotService.create(context))

    override fun getSourceType(): Source {
        return Source.MANGAKALOT
    }

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, MangaType.RECENTLY),
            pagingSourceFactory = { db.mangaDao().getFrom(SOURCE, MangaType.RECENTLY) }
        ).flow
    }

    override fun getTrendingManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, MangaType.TRENDING),
            pagingSourceFactory = { db.mangaDao().getFrom(SOURCE, MangaType.TRENDING) }
        ).flow
    }

    override fun getTopManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, MangaType.TOP),
            pagingSourceFactory = { db.mangaDao().getFrom(SOURCE, MangaType.TOP) }
        ).flow
    }

    override fun getNewManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, MangaType.NEW),
            pagingSourceFactory = { db.mangaDao().getFrom(SOURCE, MangaType.NEW) }
        ).flow
    }

    override fun getSimpleSearch(query: String): Flow<PagingData<SearchResult>> {
        // Replaces whitespaces between words with underscore. Otherwise it will become %20 which is invalid in search
        // Example query: Gakuen    Alice
        // Valid URL: https://manganelo.com/search/story/gakuen_alice
        val encodedQuery = query.replace("\\s".toRegex(), "_")

        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = SimpleSearchResultMediator(api, db, encodedQuery, SOURCE),
            pagingSourceFactory = { db.searchResultDao().getAll() }
        ).flow
    }

    override fun getSimpleSearchWithGenre(query: String, genre: GenreConstants): Flow<PagingData<SearchResult>> {
        return super.getSimpleSearchWithGenre(query, genre)
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

    companion object {
        private const val PAGINATION_SIZE = 30
        private val SOURCE = Source.MANGAKALOT
    }

}