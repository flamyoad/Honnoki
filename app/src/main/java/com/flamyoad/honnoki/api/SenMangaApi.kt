package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.network.SenMangaService
import com.flamyoad.honnoki.parser.SenMangaParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SenMangaApi(private val service: SenMangaService): BaseApi {
    private val parser = SenMangaParser()

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val url = response.string()
            val mangaList = parser.parseForRecentMangas(url)

            return@withContext mangaList
        }
    }

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        return emptyList()
    }
}