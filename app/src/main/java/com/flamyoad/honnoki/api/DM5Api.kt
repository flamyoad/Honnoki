package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.DM5Service
import com.flamyoad.honnoki.parser.DM5Parser
import com.flamyoad.honnoki.utils.extensions.stringSuspending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class DM5Api(
    private val service: DM5Service,
    private val parser: DM5Parser,
    private val apiHandler: ApiRequestHandler
) : BaseApi() {

    /**
     * DM5 uses epoch time (13 digits) to fetch recently updated manga from their server
     */
    private var epochTimeMillis = System.currentTimeMillis()

    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        if (index > MAXIMUM_DATE_INDEX) {
            return State.Success(emptyList())
        }

        val response = apiHandler.safeApiCall { service.getLatestManga(epochTimeMillis, index) }
        when (response) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForRecentMangas(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        // Only have first page, otherwise return empty list
        if (index > 1) {
            return State.Success(emptyList())
        }

        when (val response = apiHandler.safeApiCall { service.getTrendingManga(2) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForTrendingManga(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "&page=$index"
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeyword(html, index) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): State<List<SearchResult>> {
        // https://www.dm5.com/manhua-list-tag17-p3/
        val link = StringBuilder()
            .append(param.removeSuffix("/"))
            .append("-p")
            .append(index.toString())
            .toString()

        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForMangaFromGenrePage(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): State<List<SearchResult>> {
        when (val response = apiHandler.safeApiCall { service.searchByKeyword(keyword, index) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeyword(html, index) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForMangaOverview(link: String): State<MangaOverview> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForMangaOverview(html, link) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForGenres(link: String): State<List<Genre>> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForGenres(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForAuthors(link: String): State<List<Author>> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForAuthors(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForChapterList(link: String): State<List<Chapter>> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForChapterList(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForImageList(relativeLink: String): State<List<Page>> {
        val absoluteLink = DM5Service.BASE_MOBILE_URL + relativeLink

        when (val response = apiHandler.safeApiCall { service.getHtml(absoluteLink) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForImageList(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    companion object {
        const val MAXIMUM_DATE_INDEX = 6

        /**
         * Constructs an URL pointing to the website that contains manga of selected genre
         * Example: https://www.dm5.com/manhua-list-tag1/
         */
        fun getDm5GenreUrl(genre: GenreConstants): String {
            val genreId = getDm5GenreId(genre)
            if (genreId == -1) return ""

            return DM5Service.BASE_URL + "manhua-list-tag$genreId/"
        }

        /**
         * Get the id of genre in DM5's database
         */
        fun getDm5GenreId(genre: GenreConstants): Int {
            return when (genre) {
                GenreConstants.ACTION -> 31 // 热血
                GenreConstants.ROMANCE -> 26 // 恋爱
                GenreConstants.SCHOOL_LIFE -> 1 // 校园
                GenreConstants.YURI -> 3 // 百合
                GenreConstants.YAOI -> 27 // 彩虹
                GenreConstants.ADVENTURE -> 2 // 冒险
                GenreConstants.HAREM -> 8 // 后宫
                GenreConstants.SCIFI -> 25 // 科幻
                GenreConstants.MARTIAL_ARTS -> 12 // 战争
                GenreConstants.PSYCHOLOGICAL -> 17 // 悬疑
                GenreConstants.MYSTERY -> 33 // 推理
                GenreConstants.COMEDY -> 37 // 搞笑
                GenreConstants.FANTASY -> 14 // 奇幻
                GenreConstants.SUPERNATURAL -> 15 // 魔法
                GenreConstants.HORROR -> 29 // 恐怖
                GenreConstants.SEINEN -> 20 // 神鬼 ：）
                GenreConstants.HISTORICAL -> 4 // 历史
                GenreConstants.DOUJINSHI -> 30 // 同人
                GenreConstants.SPORTS -> 34 // 运动
                GenreConstants.ECCHI -> 36 // 绅士
                GenreConstants.MECHA -> 40 // 机甲
                GenreConstants.ADULT -> 61 // 限制级
                else -> -1
            }
        }
    }
}