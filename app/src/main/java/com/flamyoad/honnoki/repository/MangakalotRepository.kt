package com.flamyoad.honnoki.repository

import android.webkit.WebSettings
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.MangakalotApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.Source
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.paging.MangakalotRemoteMediator
import kotlinx.coroutines.flow.Flow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class MangakalotRepository(private val db: AppDatabase) : BaseMangaRepository {
    private val service: MangakalotService
    private val api: MangakalotApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(MangakalotService.baseUrl)
            .build()

        service = retrofit.create(MangakalotService::class.java)

        api = MangakalotApi(service)
    }

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        val pagingSourceFactory = { db.mangaDao().getFrom(Source.MANGAKALOT) }

        return Pager(
            config = PagingConfig(pageSize = PAGINATION_SIZE, enablePlaceholders = false),
            remoteMediator = MangakalotRemoteMediator(api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        private const val PAGINATION_SIZE = 30
    }

}