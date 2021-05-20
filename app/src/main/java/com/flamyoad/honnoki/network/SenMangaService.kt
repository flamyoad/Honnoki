package com.flamyoad.honnoki.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface SenMangaService {
    companion object {
        const val BASE_URL = "https://raw.senmanga.com/"
    }

    @GET("directory/last_update")
    suspend fun getLatestManga(@Query("page") index: Int): ResponseBody

    @GET(".")
    suspend fun getTrendingManga(@Query("page") index: Int): ResponseBody
}