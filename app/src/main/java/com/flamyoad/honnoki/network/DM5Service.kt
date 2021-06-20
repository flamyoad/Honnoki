package com.flamyoad.honnoki.network

import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.*

private const val CACHE_CONTROL_MAX_AGE_60 = "Cache-Control: max-age=60"
private const val CACHE_CONTROL_NO_CACHE = "Cache-Control: no-cache"

interface DM5Service {
    companion object {
        const val BASE_URL = "https://dm5.com/"
        const val BASE_MOBILE_URL = "https://m.dm5.com/"
        const val CACHE_SIZE = (5 * 1024 * 1024).toLong()
    }

    /**
     * Parameter dateIndex should be only between 0 ~ 6 (inclusive).
     */
    @POST("manhua-new/dm5.ashx")
    @FormUrlEncoded
    suspend fun getLatestManga(
        @Query("d") epochMillis: Long,
        @Field("DK") dateIndex: Int,
        @Query("action") action: String = "getupdatecomics",
        @Field("page") page: Int = 1,
        @Field("pagesize") pageSize: Int = 139
    ): ResponseBody

    @GET("search")
    suspend fun searchByKeyword(
        @Query("title") keyword: String,
        @Query("page") index: Int,
        @Query("language") language: Int = 1
    ): ResponseBody
    @GET
    @Headers(CACHE_CONTROL_MAX_AGE_60)
    suspend fun getHtml(@Url url: String): ResponseBody
}