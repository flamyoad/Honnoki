package com.flamyoad.honnoki.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

private const val CACHE_CONTROL_MAX_AGE_60 = "Cache-Control: max-age=60"
private const val CACHE_CONTROL_NO_CACHE = "Cache-Control: no-cache"

interface MangaTownService {
    companion object {
        const val BASE_URL = "https://mangatown.com/"
        const val CACHE_SIZE = (2 * 1024 * 1024).toLong()
    }

    @GET("latest/{index}.htm")
    suspend fun getLatestManga(@Path("index") index: Int): ResponseBody

    @GET("hot/{index}.htm")
    suspend fun getTrendingManga(@Path("index") index: Int): ResponseBody

    @GET
    @Headers(CACHE_CONTROL_MAX_AGE_60)
    suspend fun getMangaOverview(@Url url: String): ResponseBody
}