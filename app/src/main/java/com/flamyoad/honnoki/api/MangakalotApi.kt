package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.parser.MangakalotParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class MangakalotApi(private val service: MangakalotService) {
    private val parser = MangakalotParser()

    suspend fun searchForLatestManga(index: Int): List<Manga> {
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val url = response.string()
            val mangaList = parser.parseForRecentMangas(url)

            return@withContext mangaList
        }
    }
}