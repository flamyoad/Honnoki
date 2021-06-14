package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.MangaOverview
import com.flamyoad.honnoki.data.model.State
import com.flamyoad.honnoki.network.SenMangaService
import com.flamyoad.honnoki.parser.SenMangaParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SenMangaApi(
    private val service: SenMangaService,
    private val parser: SenMangaParser
) : BaseApi() {

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val url = response.string()
            val mangaList = parser.parseForRecentMangas(url)

            return@withContext mangaList
        }
    }

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val url = response.string()
            val mangaList = parser.parseForTrendingMangas(url)

            return@withContext mangaList
        }    }

    suspend fun searchForOverview(link: String): State<MangaOverview> {
        val response = try {
            service.getMangaOverview(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val mangaOverview = parser.parseForMangaOverview(html, link)
            return@withContext State.Success(mangaOverview)
        }
    }
}