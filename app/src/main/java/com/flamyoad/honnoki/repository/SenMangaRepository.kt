package com.flamyoad.honnoki.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.SenMangaApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.MangaType
import com.flamyoad.honnoki.model.Source
import com.flamyoad.honnoki.network.SenMangaService
import com.flamyoad.honnoki.paging.MangaRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import retrofit2.Retrofit

class SenMangaRepository(db: AppDatabase): BaseMangaRepository(db) {
    private val service: SenMangaService
    private val api: SenMangaApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(SenMangaService.baseUrl)
            .build()

        service = retrofit.create(SenMangaService::class.java)

        api = SenMangaApi(service)
    }

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = MangaRemoteMediator(api, db, MangaType.RECENTLY),
            pagingSourceFactory = { db.mangaDao().getFrom(Source.SENMANGA, MangaType.RECENTLY) }
        ).flow
    }

    override fun getTrendingManga(): Flow<PagingData<Manga>> {
        return emptyFlow()
    }

    override fun getSourceType(): Source {
        return Source.SENMANGA
    }

    companion object {
        private const val PAGINATION_SIZE = 40
    }
}