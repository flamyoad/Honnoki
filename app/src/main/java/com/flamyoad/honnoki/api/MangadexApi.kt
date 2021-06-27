package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.MangadexService
import com.flamyoad.honnoki.parser.MangadexParser
import com.flamyoad.honnoki.parser.exception.NullMangaIdException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

class MangadexApi(
    private val service: MangadexService,
    private val parser: MangadexParser
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        val offset = index * PAGINATION_SIZE

        val json = try {
            service.getRecentlyAddedManga(offset, PAGINATION_SIZE, ORDER_DESC)
        } catch (e: IOException) {
            return emptyList()
        }

        val failToGetResults = json.results?.firstOrNull()?.result != RESULT_OK
        if (failToGetResults) {
            return emptyList()
        }

        return withContext(Dispatchers.Default) {
            parser.parseHomeMangas(json, MangaType.RECENTLY)
        }
    }

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        val offset = index * PAGINATION_SIZE

        val json = try {
            service.getTopManga(offset, PAGINATION_SIZE)
        } catch (e: IOException) {
            return emptyList()
        }

        return withContext(Dispatchers.Default) {
            parser.parseHomeMangas(json, MangaType.TRENDING)
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): List<SearchResult> {
        val offset = index * PAGINATION_SIZE
        val json = try {
            service.searchByKeyword(keyword, offset, PAGINATION_SIZE)
        } catch (e: IOException) {
            return emptyList()
        }

        return withContext(Dispatchers.Default) {
            parser.parseForSearchResult(json)
        }
    }

    suspend fun searchForMangaOverview(mangaId: String): State<MangaOverview> {
        val json = try {
            service.getMangaDetails(mangaId)
        } catch (e: IOException) {
            return State.Error(e)
        }

        if (json.result != RESULT_OK) {
            return State.Error()
        }

        try {
            val overview = parser.parseForMangaOverview(json)
            return State.Success(overview)

        } catch (e: Exception) {
            if (e is NullMangaIdException)
                return State.Error()
            else
                throw e // Rethrow unknown exceptions
        }
    }

    suspend fun searchForGenres(mangaId: String): State<List<Genre>> {
        val json = service.getMangaDetails(mangaId)

        if (json.result != RESULT_OK) {
            return State.Error()
        }

        return withContext(Dispatchers.Default) {
            val genres = parser.parseForGenres(json)
            return@withContext State.Success(genres)
        }
    }

    /**
     * Since Mangadex provides both Artist and Authors, but our app only shows Artists
     * So we just combine both of them into one
     */
    suspend fun searchForAuthors(mangaId: String): State<List<Author>> {
        val json = service.getMangaDetails(mangaId)

        if (json.result != RESULT_OK) {
            return State.Error()
        }

        return withContext(Dispatchers.Default) {
            val genres = parser.parseForAuthors(json)
            return@withContext State.Success(genres)
        }
    }

    /**
     * MD limits each chapter request to 100 items each. Therefore, we have to make requests
     * in a recursive manner until offset is larger than total
     */
    suspend fun searchForChapterList(mangaId: String): State<List<Chapter>> {
        val json = service.getChapterList(mangaId, limit = CHAPTER_MAX_LIMIT, offset = 0)

        var offset = json.offset
        var total = json.total

        val chapterList = mutableListOf<Chapter>()

        chapterList.addAll(parser.parseForChapters(json, offset))

        if (offset < total) {
            offset += CHAPTER_MAX_LIMIT
            val nextJson = service.getChapterList(mangaId, limit = CHAPTER_MAX_LIMIT, offset = offset)
            chapterList.addAll(parser.parseForChapters(nextJson, offset))
        }

        return State.Success(chapterList)
    }

    /**
     * [offset]: Pass in "number" field in the Chapter object. It contains the offset
     *           of the item when fetching chapter list
     */
    suspend fun searchForImageList(chapterId: String): State<List<Page>> {
        val chapterJson = service.getPages(chapterId)
        val baseUrlJson = service.getBaseUrl(chapterJson.data.id)

        return withContext(Dispatchers.Default) {
            val imageList = parser.parseForImageList(chapterJson, baseUrlJson)
            return@withContext State.Success(imageList)
        }
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