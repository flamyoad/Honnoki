package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.data.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.DM5Service
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.parser.DM5Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class DM5Api(
    private val service: DM5Service,
    private val parser: DM5Parser
) : BaseApi() {

    /**
     * DM5 uses epoch time (13 digits) to fetch recently updated manga from their server
     */
    private var epochTimeMillis = System.currentTimeMillis()

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

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        // Only have first page, otherwise return empty list
        if (index > 1) {
            return emptyList()
        }

        val response = service.getTrendingManga(2) // Japanese manga category only

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForTrendingManga(html)

            return@withContext mangaList
        }
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): List<SearchResult> {
        val link = param + "&page=$index"
        val response = service.getHtml(link)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeyword(html, index)

            return@withContext searchResultList
        }
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): List<SearchResult> {
        // https://www.dm5.com/manhua-list-tag17-p3/
        val link = StringBuilder()
            .append(param.removeSuffix("/"))
            .append("-p")
            .append(index.toString())
            .toString()

        val response = service.getHtml(link)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForMangaFromGenrePage(html)

            return@withContext searchResultList
        }
    }

    suspend fun searchForMangaOverview(link: String): State<MangaOverview> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val mangaOverview = parser.parseForMangaOverview(html, link)
            return@withContext State.Success(mangaOverview)
        }
    }

    suspend fun searchForGenres(link: String): State<List<Genre>> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val genres = parser.parseForGenres(html)
            return@withContext State.Success(genres)
        }
    }

    suspend fun searchForAuthors(link: String): State<List<Author>> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val genres = parser.parseForAuthors(html)
            return@withContext State.Success(genres)
        }
    }

    suspend fun searchForChapterList(link: String): State<List<Chapter>> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val chapterList = parser.parseForChapterList(html)
            return@withContext State.Success(chapterList)
        }
    }

    suspend fun searchForImageList(relativeLink: String): State<List<Page>> {
        val absoluteLink = DM5Service.BASE_MOBILE_URL + relativeLink

        val response = try {
            service.getHtml(absoluteLink)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val imageList = parser.parseForImageList(html)
            return@withContext State.Success(imageList)
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): List<SearchResult> {
        val response = service.searchByKeyword(keyword, index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeyword(html, index)

            return@withContext searchResultList
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