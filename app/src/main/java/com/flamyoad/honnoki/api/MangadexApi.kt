package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.MangadexService
import com.flamyoad.honnoki.parser.MangadexParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class MangadexApi(
    private val service: MangadexService,
    private val parser: MangadexParser,
    private val apiHandler: ApiRequestHandler
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        val offset = index * PAGINATION_SIZE

        val response = apiHandler.safeApiCall {
            service.getRecentlyAddedManga(offset, PAGINATION_SIZE, ORDER_DESC)
        }

        when (response) {
            is NetworkResult.Success -> {
                return successOrErrorIfNull {
                    parser.parseHomeMangas(response.data, MangaType.RECENTLY)
                }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        val offset = index * PAGINATION_SIZE

        when (val response =
            apiHandler.safeApiCall { service.getTopManga(offset, PAGINATION_SIZE) }) {
            is NetworkResult.Success -> {
                return successOrErrorIfNull {
                    parser.parseHomeMangas(response.data, MangaType.TRENDING)
                }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): State<List<SearchResult>> {
        val offset = PAGINATION_SIZE * (index)

        val response =
            apiHandler.safeApiCall { service.searchByKeyword(keyword, offset, PAGINATION_SIZE) }
        when (response) {
            is NetworkResult.Success -> {
                return successOrErrorIfNull { parser.parseForSearchResult(response.data) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForMangaOverview(mangaId: String): State<MangaOverview> {
        when (val response = apiHandler.safeApiCall { service.getMangaDetails(mangaId) }) {
            is NetworkResult.Success -> {
                return if (response.data.result == RESULT_OK) {
                    val overview = parser.parseForMangaOverview(response.data)
                    State.Success(overview)
                } else {
                    State.Error()
                }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForGenres(mangaId: String): State<List<Genre>> {
        when (val response = apiHandler.safeApiCall { service.getMangaDetails(mangaId) }) {
            is NetworkResult.Success -> {
                return if (response.data.result == RESULT_OK) {
                    val genres = parser.parseForGenres(response.data)
                    State.Success(genres)
                } else {
                    State.Error()
                }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    /**
     * Since Mangadex provides both Artist and Authors, but our app only shows Artists
     * So we just combine both of them into one
     */
    suspend fun searchForAuthors(mangaId: String): State<List<Author>> {
        when (val response = apiHandler.safeApiCall { service.getMangaDetails(mangaId) }) {
            is NetworkResult.Success -> {
                return if (response.data.result == RESULT_OK) {
                    val genres = parser.parseForAuthors(response.data)
                    return State.Success(genres)
                } else {
                    State.Error()
                }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    /**
     * MD limits each chapter request to 100 items each. Therefore, we have to make requests
     * in a recursive manner until offset is larger than total
     */
    suspend fun searchForChapterList(mangaId: String): State<List<Chapter>> {
        var offset = 0
        var total = 0
        val chapterList = mutableListOf<Chapter>()

        do {
            val response = apiHandler.safeApiCall {
                service.getChapterList(mangaId, limit = CHAPTER_MAX_LIMIT, offset = offset)
            }
            when (response) {
                is NetworkResult.Success -> {
                    offset = response.data.offset + CHAPTER_MAX_LIMIT
                    total = response.data.total
                    chapterList.addAll(parser.parseForChapters(response.data, offset))
                }
                is NetworkResult.Failure -> {
                    break
                }
            }
        } while (offset < total)

        return State.Success(chapterList)
    }

    /**
     * [offset]: Pass in "number" field in the Chapter object. It contains the offset
     *           of the item when fetching chapter list
     */
    suspend fun searchForImageList(chapterId: String): State<List<Page>> {
        val chapterJson =
            when (val response = apiHandler.safeApiCall { service.getPages(chapterId) }) {
                is NetworkResult.Success -> response.data
                is NetworkResult.Failure -> return State.Error(response.exception)
            }

        val baseUrlJson = when (val response =
            apiHandler.safeApiCall { service.getBaseUrl(chapterJson.data.id) }) {
            is NetworkResult.Success -> response.data
            is NetworkResult.Failure -> return State.Error(response.exception)
        }

        val imageList = parser.parseForImageList(chapterJson, baseUrlJson)
        return State.Success(imageList)
    }

    companion object {
        const val PAGINATION_SIZE = 50
        const val ORDER_DESC = "desc"
        const val ORDER_ASC = "asc"
        const val RESULT_OK = "ok"
        const val COVER_ART = "cover_art"

        // Limit must be <= 100 when calling the chapter endpoint
        const val CHAPTER_MAX_LIMIT = 100
    }
}