package com.flamyoad.honnoki.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.MangakalotApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.*
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.paging.MangaRemoteMediator
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit

@ExperimentalPagingApi
class MangakalotRepository(db: AppDatabase) : BaseMangaRepository(db) {
    private val api: MangakalotApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(MangakalotService.baseUrl)
            .build()

        val service = retrofit.create(MangakalotService::class.java)
        api = MangakalotApi(service)
    }

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = MangaRemoteMediator(api, db, MangaType.RECENTLY),
            pagingSourceFactory = { db.mangaDao().getFrom(Source.MANGAKALOT, MangaType.RECENTLY) }
        ).flow
    }

    override fun getTrendingManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = MangaRemoteMediator(api, db, MangaType.TRENDING),
            pagingSourceFactory = { db.mangaDao().getFrom(Source.MANGAKALOT, MangaType.TRENDING) }
        ).flow
    }

    override fun getSourceType(): Source {
        return Source.MANGAKALOT
    }

    override suspend fun getMangaOverview(urlPath: String): State<MangaOverview> {
        return api.searchForMangaOverview(urlPath)
    }

    override suspend fun getChapterList(urlPath: String): State<List<Chapter>> {
        return api.searchForChapterList(urlPath)
    }

    companion object {
        private const val PAGINATION_SIZE = 30
    }

}