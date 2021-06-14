package com.flamyoad.honnoki.api

import android.util.Log
import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.network.MangaTownService
import com.flamyoad.honnoki.parser.MangaTownParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangaTownApi(
    private val service: MangaTownService,
    private val parser: MangaTownParser
): BaseApi() {

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForRecentMangas(html)

            return@withContext mangaList
        }
    }

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        val response = service.getTrendingManga(index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForTrendingMangas(html)

            return@withContext mangaList
        }
    }
}