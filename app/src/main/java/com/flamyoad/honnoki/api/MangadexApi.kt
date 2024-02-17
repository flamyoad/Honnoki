package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.DynamicGenre
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.MangadexService
import com.flamyoad.honnoki.parser.MangadexParser

class MangadexApi(
    private val service: MangadexService,
    private val parser: MangadexParser,
    apiHandler: ApiRequestHandler
) : BaseApi(apiHandler) {

    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        val offset = index * PAGINATION_SIZE
        return processApiData(
            apiCall = {
                service.getRecentlyAddedManga(
                    offset,
                    limit = PAGINATION_SIZE,
                    order = ORDER_DESC,
                    includes = listOf(AUTHOR, ARTIST, COVER_ART)
                )
            },
            parseData = { parser.parseHomeMangas(it, MangaType.RECENTLY) }
        )
    }

    override suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        val offset = index * PAGINATION_SIZE
        return processApiData(
            apiCall = {
                service.getTopManga(
                    offset,
                    limit = PAGINATION_SIZE,
                    includes = listOf(COVER_ART)
                )
            },
            parseData = { parser.parseHomeMangas(it, MangaType.TRENDING) }
        )
    }

    override suspend fun searchByKeyword(
        keyword: String,
        index: Int
    ): State<List<SearchResult>> {
        val offset = PAGINATION_SIZE * (index)
        return processApiData(
            apiCall = {
                service.searchByKeyword(
                    keyword,
                    offset,
                    PAGINATION_SIZE
                )
            },
            parseData = { parser.parseForSearchResult(it) }
        )
    }

    suspend fun searchForMangaOverview(mangaId: String): State<MangaOverview> {
        return processApiData(
            apiCall = {
                service.getMangaDetails(
                    mangaId,
                    includes = listOf(AUTHOR, ARTIST, COVER_ART)
                )
            },
            parseData = { parser.parseForMangaOverview(it) }
        )
    }

    suspend fun searchForGenres(mangaId: String): State<List<Genre>> {
        return processApiData(
            apiCall = {
                service.getMangaDetails(
                    mangaId,
                    includes = listOf(AUTHOR, ARTIST, COVER_ART)
                )
            },
            parseData = { parser.parseForGenres(it) }
        )
    }

    /**
     * Since Mangadex provides both Artist and Authors, but our app only shows Artists
     * So we just combine both of them into one
     */
    suspend fun searchForAuthors(mangaId: String): State<List<Author>> {
        return processApiData(
            apiCall = {
                service.getMangaDetails(
                    mangaId,
                    includes = listOf(AUTHOR, ARTIST, COVER_ART)
                )
            },
            parseData = { parser.parseForAuthors(it.data) }
        )
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
            val result = processApiData(
                apiCall = {
                    service.getChapterList(
                        mangaId,
                        limit = CHAPTER_MAX_LIMIT,
                        offset = offset
                    )
                },
                parseData = {
                    offset = it.offset + CHAPTER_MAX_LIMIT
                    total = it.total
                    parser.parseForChapters(it, offset)
                }
            )
            when (result) {
                is State.Success -> chapterList.addAll(result.value)
                else -> break
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
            when (val response =
                apiHandler.safeApiCall { service.getPages(chapterId) }) {
                is NetworkResult.Success -> response.data
                is NetworkResult.Failure -> return State.Error(response.exception)
            }

        val baseUrlJson = when (val response =
            apiHandler.safeApiCall {
                service.getBaseUrl(
                    chapterJson.data?.id ?: ""
                )
            }) {
            is NetworkResult.Success -> response.data
            is NetworkResult.Failure -> return State.Error(response.exception)
        }

        val imageList = parser.parseForImageList(baseUrlJson)
        return State.Success(imageList)
    }

    override suspend fun searchMangaByAuthor(
        param: String,
        index: Int
    ): State<List<SearchResult>> {
        val offset = index * PAGINATION_SIZE
        return processApiData(
            apiCall = {
                service.getMangaByAuthor(
                    offset,
                    PAGINATION_SIZE,
                    authorId = param,
                    artistId = param,
                    orderLatestUploadedChapter = ORDER_DESC,
                    includes = listOf(COVER_ART)
                )
            },
            parseData = { parser.parseForSearchResult(it) }
        )
    }

    override suspend fun searchMangaByGenre(
        param: String,
        index: Int
    ): State<List<SearchResult>> {
        val offset = index * PAGINATION_SIZE
        return processApiData(
            apiCall = {
                service.getMangaByGenre(
                    offset,
                    PAGINATION_SIZE,
                    genreId = param,
                    orderLatestUploadedChapter = ORDER_DESC,
                    includes = listOf(COVER_ART)
                )
            },
            parseData = { parser.parseForSearchResult(it) }
        )
    }

    override suspend fun getAvailableGenres(): State<List<DynamicGenre>> {
        return processApiData(
            apiCall = { service.getAllTags() },
            parseData = { parser.parseForDynamicGenres(it) }
        )
    }

    companion object {
        const val PAGINATION_SIZE = 50
        const val ORDER_DESC = "desc"
        const val ORDER_ASC = "asc"
        const val RESULT_OK = "ok"
        const val COVER_ART = "cover_art"
        const val AUTHOR = "author"
        const val ARTIST = "artist"

        // Limit must be <= 100 when calling the chapter endpoint
        const val CHAPTER_MAX_LIMIT = 100
    }
}