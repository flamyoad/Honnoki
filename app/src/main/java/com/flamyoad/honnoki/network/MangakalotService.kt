package com.flamyoad.honnoki.network

import okhttp3.ResponseBody
import retrofit2.http.*

private const val CACHE_CONTROL_MAX_AGE_60 = "Cache-Control: max-age=60"
private const val CACHE_CONTROL_NO_CACHE = "Cache-Control: no-cache"

// Network interceptor is called only when accessing network, so cached responses will not be displayed in log
interface MangakalotService {
    companion object {
        const val BASE_URL = "https://manganelo.com/"
        const val CACHE_SIZE = (2 * 1024 * 1024).toLong()
    }

    @GET("genre-all/{index}")
    suspend fun getLatestManga(@Path("index") index: Int): ResponseBody

    @GET("genre-all/{index}?type=topview")
    suspend fun getTrendingManga(@Path("index") index: Int): ResponseBody

    @GET("genre-all/{index}?type=newest")
    suspend fun getNewManga(@Path("index") index: Int): ResponseBody

    @GET("genre-all/1")
    suspend fun getTopWeekManga(): ResponseBody

    @GET
    @Headers(CACHE_CONTROL_MAX_AGE_60)
    suspend fun getHtml(@Url url: String): ResponseBody

    @GET
    @Headers(CACHE_CONTROL_NO_CACHE)
    suspend fun getHtmlNoCache(@Url url: String): ResponseBody

    // https://manganato.com/search/story/one_piece
    @GET("search/story/{keyword}")
    suspend fun searchByKeyword(
        @Path("keyword") keyword: String,
        @Query("page") index: Int
    ): ResponseBody

    // https://manganato.com/advanced_search?s=all&g_i=_2_&page=1&keyw=manji
    @GET("advanced_search")
    suspend fun searchByKeywordAndGenres(
        @Query("s") mode: String = "all",
        @Query("g_i") genre: String,
        @Query("page") index: Int,
        @Query("keyw") keyword: String): ResponseBody
}