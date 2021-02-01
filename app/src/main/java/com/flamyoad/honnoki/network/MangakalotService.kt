package com.flamyoad.honnoki.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface MangakalotService {
    companion object {
        const val baseUrl = "https://mangakakalot.com"
    }

    @GET("/manga_list?type=latest&category=all&state=all")
    suspend fun getLatestManga(@Query("page") index: Int): ResponseBody

    @GET("/manga_list?type=topview&category=all&state=all")
    suspend fun getPopularManga(@Query("page") index: Int): ResponseBody
}