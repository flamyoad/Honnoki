package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.BuildConfig
import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.data.GenreConstants

abstract class BaseApi(val apiHandler: ApiRequestHandler) {
    abstract val startingPageIndex: Int

    // Safeguard against any leaked exceptions in Jsoup parser
    suspend inline fun <A, T : Any> processApiData(
        crossinline apiCall: suspend () -> A,
        crossinline parseData: suspend (responseData: A) -> T,
    ): State<T> {

        when (val apiResult = apiHandler.safeApiCall { apiCall() }) {
            is NetworkResult.Success -> {
                return try {
                    val parsingResult = parseData(apiResult.data)
                    State.Success(parsingResult)
                } catch (ex: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw ex
                    } else {
                        State.Error(ex)
                    }
                }
            }
            is NetworkResult.Failure -> {
                return State.Error(apiResult.exception)
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