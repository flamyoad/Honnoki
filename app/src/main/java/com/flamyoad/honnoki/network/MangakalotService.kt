package com.flamyoad.honnoki.network

import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface MangakalotService {
    companion object {
        const val baseUrl = "https://manganelo.com/"
    }

    @GET("genre-all/{index}")
    suspend fun getLatestManga(@Path("index") index: Int): ResponseBody

    @GET("genre-all/{index}?type=topview")
    suspend fun getTrendingManga(@Path("index") index: Int): ResponseBody

    @GET
    suspend fun getMangaOverview(@Url url: String): ResponseBody
}