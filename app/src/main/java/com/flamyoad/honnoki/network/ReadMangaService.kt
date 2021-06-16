package com.flamyoad.honnoki.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


private const val CACHE_CONTROL_MAX_AGE_60 = "Cache-Control: max-age=60"
private const val CACHE_CONTROL_NO_CACHE = "Cache-Control: no-cache"

interface ReadMangaService {
    companion object {
        const val BASE_URL = "https://readmng.com/"
        const val CACHE_SIZE = (2 * 1024 * 1024).toLong()
    }

    @GET("latest-releases/{index}")
    suspend fun getLatestManga(@Path("index") index: Int): ResponseBody

    @GET("hot-manga/{index}")
    suspend fun getTrendingManga(@Path("index") index: Int): ResponseBody

    @GET
    @Headers(CACHE_CONTROL_MAX_AGE_60)
    suspend fun getHtml(@Url url: String): ResponseBody

    @POST("service/advanced_search")
    @FormUrlEncoded
    @Headers(
        "X-Requested-With: XMLHttpRequest",
        "content-type:application/x-www-form-urlencoded; charset=UTF-8"
    )
    suspend fun searchByKeyword(
        @Field("manga-name") query: String,
        @Field("type") type: String = "all",
        @Field("status") status: String = "both",
    ): ResponseBody
}