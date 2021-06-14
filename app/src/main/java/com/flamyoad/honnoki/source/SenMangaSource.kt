package com.flamyoad.honnoki.source

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.SenMangaApi
import com.flamyoad.honnoki.data.model.*
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.network.SenMangaService
import com.flamyoad.honnoki.paging.MangaMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import retrofit2.Retrofit


@ExperimentalPagingApi
class SenMangaSource(db: AppDatabase, context: Context, private val api: SenMangaApi): BaseSource(db, context) {

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, MangaType.RECENTLY),
            pagingSourceFactory = { db.mangaDao().getFrom(Source.SENMANGA, MangaType.RECENTLY) }
        ).flow
    }

    override fun getTrendingManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, MangaType.TRENDING),
            pagingSourceFactory = { db.mangaDao().getFrom(Source.SENMANGA, MangaType.TRENDING) }
        ).flow
    }

    override fun getSourceType(): Source {
        return Source.SENMANGA
    }

    override suspend fun getChapterList(urlPath: String): State<List<Chapter>> {
        return State.Error()
    }

    override fun getSimpleSearch(query: String): Flow<PagingData<SearchResult>> {
        return emptyFlow()
    }

    override suspend fun getMangaOverview(urlPath: String): State<MangaOverview> {
        return api.searchForOverview(urlPath)
    }

    companion object {
        private const val PAGINATION_SIZE = 40
    }
}