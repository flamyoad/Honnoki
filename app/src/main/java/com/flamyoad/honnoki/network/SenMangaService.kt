package com.flamyoad.honnoki.network

import okhttp3.ResponseBody
import retrofit2.http.*

private const val CACHE_CONTROL_MAX_AGE_60 = "Cache-Control: max-age=60"
private const val CACHE_CONTROL_NO_CACHE = "Cache-Control: no-cache"

interface SenMangaService {
    companion object {
        const val BASE_URL = "https://raw.senmanga.com/"
        const val CACHE_SIZE = (2 * 1024 * 1024).toLong()
    }

    @GET("directory/last_update")
    suspend fun getLatestManga(@Query("page") index: Int): ResponseBody

    @GET("directory/popular")
    suspend fun getTrendingManga(@Query("page") index: Int): ResponseBody

    @GET
    @Headers(CACHE_CONTROL_MAX_AGE_60)
    suspend fun getHtml(@Url url: String): ResponseBody
}