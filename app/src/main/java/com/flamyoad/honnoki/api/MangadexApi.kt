package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.json.mangadex.relationships.RelCoverImage
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.network.MangadexService
import com.flamyoad.honnoki.parser.MangadexParser

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

        return parser.parseForHomePageMangas(json, MangaType.RECENTLY)
    }

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        val offset = index * PAGINATION_SIZE
        val json = service.getTopManga(offset, PAGINATION_SIZE)

        val failToGetResults = json.results?.firstOrNull()?.result != RESULT_OK
        if (failToGetResults) {
            return emptyList()
        }

        return parser.parseForHomePageMangas(json, MangaType.TRENDING)
    }

    companion object {
        const val PAGINATION_SIZE = 50
        const val ORDER_DESC = "desc"
        const val ORDER_ASC = "asc"
        const val RESULT_OK = "ok"
        const val COVER_ART = "cover_art"
    }
}