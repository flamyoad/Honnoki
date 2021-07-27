package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.BuildConfig
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.data.GenreConstants

abstract class BaseApi {
    abstract val startingPageIndex: Int

    // Safeguard against any leaked exceptions in Jsoup parser
    suspend inline fun <T : Any> successOrErrorIfNull(crossinline parse: suspend () -> T): State<T> {
        return try {
            State.Success((parse.invoke()))
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            } else {
                State.Error(e)
            }
        }
    }

    open suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        return State.Success(emptyList())
    }

    open suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        return State.Success(emptyList())
    }

    open suspend fun searchForTopManga(index: Int): State<List<Manga>> {
        return State.Success(emptyList())
    }

    open suspend fun searchForNewManga(index: Int): State<List<Manga>> {
        return State.Success(emptyList())
    }

    open suspend fun searchByKeyword(keyword: String, index: Int): State<List<SearchResult>> {
        return State.Success(emptyList())
    }

    open suspend fun searchByKeywordAndGenres(
        keyword: String, genre: GenreConstants, index: Int
    ): State<List<SearchResult>> {
        return State.Success(emptyList())
    }

    open suspend fun searchMangaByGenre(param: String, index: Int): State<List<SearchResult>> {
        return State.Success(emptyList())
    }

    open suspend fun searchMangaByAuthor(param: String, index: Int): State<List<SearchResult>> {
        return State.Success(emptyList())
    }
}