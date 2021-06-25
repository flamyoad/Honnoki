package com.flamyoad.honnoki.network

import com.flamyoad.honnoki.api.dto.mangadex.MDCoverImage
import com.flamyoad.honnoki.api.dto.mangadex.MDResult
import com.flamyoad.honnoki.api.dto.mangadex.MDResultList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MangadexService {
    companion object {
        const val BASE_URL = "https://api.mangadex.org/"
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
    suspend fun getMangaDetails(
        @Path("mangaId") mangaId: String,
        @Query("includes[]") includes1: String = "author",
        @Query("includes[]") includes2: String = "artist",
        @Query("includes[]") includes3: String = "cover_art"
    ): MDResult

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