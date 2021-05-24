package com.flamyoad.honnoki.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface MangakalotService {
    companion object {
        const val BASE_URL = "https://manganelo.com/"

        fun create(context: Context): MangakalotService {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(HttpInterceptor(BASE_URL, context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .build()
            return retrofit.create(MangakalotService::class.java)
        }
    }

    @GET("genre-all/{index}")
    suspend fun getLatestManga(@Path("index") index: Int): ResponseBody

    @GET("genre-all/{index}?type=topview")
    suspend fun getTrendingManga(@Path("index") index: Int): ResponseBody

    @GET
    suspend fun getMangaOverview(@Url url: String): ResponseBody

    @GET("search/story/{keyword}")
    suspend fun searchByKeyword(
        @Path("keyword") keyword: String,
        @Query("page") index: Int
    ): ResponseBody
}