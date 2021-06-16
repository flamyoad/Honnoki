package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.network.DM5Service
import com.flamyoad.honnoki.parser.DM5Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DM5Api(
    private val service: DM5Service,
    private val parser: DM5Parser
): BaseApi() {

    companion object {
        const val MAXIMUM_DATE_INDEX = 6
    }

    private val epochTimeMillis = System.currentTimeMillis()

    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        if (index > MAXIMUM_DATE_INDEX) {
            return emptyList()
        }

        val response = service.getLatestManga(epochTimeMillis, index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForRecentMangas(html)

            return@withContext mangaList
        }
    }
}