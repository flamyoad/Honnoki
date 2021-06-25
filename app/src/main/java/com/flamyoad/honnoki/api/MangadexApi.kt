package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.State
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.network.MangadexService
import com.flamyoad.honnoki.parser.MangadexParser
import com.flamyoad.honnoki.parser.exception.NullMangaIdException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class MangadexApi(
    private val service: MangadexService,
    private val parser: MangadexParser
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        val offset = index * PAGINATION_SIZE
        val json = service.getRecentlyAddedManga(offset, PAGINATION_SIZE, ORDER_DESC)

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
        val json = service.getTopManga(offset, PAGINATION_SIZE)

        return withContext(Dispatchers.Default) {
            parser.parseHomeMangas(json, MangaType.TRENDING)
        }
    }

    suspend fun searchForMangaOverview(mangaId: String): State<MangaOverview> {
        val json = service.getMangaDetails(mangaId)

        val failToGet = json.result != RESULT_OK
        if (failToGet) {
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

    companion object {
        const val PAGINATION_SIZE = 50
        const val ORDER_DESC = "desc"
        const val ORDER_ASC = "asc"
        const val RESULT_OK = "ok"
        const val COVER_ART = "cover_art"
    }
}