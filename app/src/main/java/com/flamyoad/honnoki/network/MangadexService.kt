package com.flamyoad.honnoki.network

import com.flamyoad.honnoki.api.dto.mangadex.*
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

private const val CACHE_CONTROL_MAX_AGE_60 = "Cache-Control: max-age=60"
private const val CACHE_CONTROL_NO_CACHE = "Cache-Control: no-cache"

interface MangadexService {
    companion object {
        const val BASE_URL = "https://api.mangadex.org/"
        const val CACHE_SIZE = (5 * 1024 * 1024).toLong()
    }

    /**
     * Used by "Recently Added" tab in the official website.
     * https://mangadex.org/titles/
     * [order]: asc, desc
     * TODO: Think of a way to concat THE INCLUDES[] into one parameter
     */
    @GET("manga")
    suspend fun getRecentlyAddedManga(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("order[createdAt]") order: String,
        @Query("includes[]") includes1: String = "author",
        @Query("includes[]") includes2: String = "artist",
        @Query("includes[]") includes3: String = "cover_art"
    ): MDResultList

    /**
     * Used by "Top" tab in the official website
     * https://mangadex.org/titles/
     */
    @GET("manga")
    suspend fun getTopManga(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): MDResultList

    @GET("manga/{mangaId}")
    @Headers(CACHE_CONTROL_MAX_AGE_60)
    suspend fun getMangaDetails(
        @Path("mangaId") mangaId: String,
        @Query("includes[]") includes1: String = "author",
        @Query("includes[]") includes2: String = "artist",
        @Query("includes[]") includes3: String = "cover_art"
    ): MDResult

    @GET("chapter")
    suspend fun getChapterList(
        @Query("manga") mangaId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): MDChapter

    @GET("chapter/{id}")
    suspend fun getPages(
        @Path("id") chapterId: String
    ): MDChapterResult

    @GET("manga")
    suspend fun searchByKeyword(
        @Query("title") keyword: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("includes[]") includes1: String = "author",
        @Query("includes[]") includes2: String = "artist",
        @Query("includes[]") includes3: String = "cover_art"
    ): MDResultList

    /**
     * Retrieves the base url to an assigned MangaDex@Home server for your client and chapter
     */
    @GET("at-home/server/{chapterId}")
    suspend fun getBaseUrl(
        @Path("chapterId") chapterId: String
    ): MDBaseUrl

    /**
     * Get cover of multiple manga in a single API call.
     * Example call:
     * https://api.mangadex.org/cover?ids[]=b6c7ce9c-e671-4f26-90b0-e592188e9cd6&ids[]=0ecefb70-e710-4f5a-94c0-d3a01db0a5a5
     */
    @GET("cover")
    suspend fun getCovers(
        @Query("ids[]") ids: List<String>
    ): MDCoverImage
}