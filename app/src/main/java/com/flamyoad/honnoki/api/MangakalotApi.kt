package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.exception.InvalidGenreException
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.parser.MangakalotParser
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.data.State
import com.flamyoad.honnoki.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangakalotApi(
    private val service: MangakalotService,
    private val parser: MangakalotParser
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 1

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
            val mangaList = parser.parseForTrendingManga(html)

            return@withContext mangaList
        }
    }

    override suspend fun searchForTopManga(index: Int): List<Manga> {
        val response = service.getTopWeekManga()

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForTopManga(html)

            return@withContext mangaList
        }
    }

    override suspend fun searchForNewManga(index: Int): List<Manga> {
        val response = service.getNewManga(index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForNewManga(html)

            return@withContext mangaList
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

    suspend fun searchForImageList(link: String): State<List<Page>> {
        val response = try {
            service.getHtml(link)
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

    override suspend fun searchByKeywordAndGenres(
        keyword: String,
        genre: GenreConstants,
        index: Int
    ): List<SearchResult> {

        if (genre == GenreConstants.ALL) {
            throw InvalidGenreException("There is no id for `All` genres! Use searchByKeyword() instead of searchByKeywordAndGenres()")
        }

        val genreString = "_" + getMangakalotGenreId(genre) + "_"
        val response = service.searchByKeywordAndGenres(
            genre = genreString,
            keyword = keyword,
            index = index
        )

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeywordAndGenre(html, index)

            return@withContext searchResultList
        }
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): List<SearchResult> {
        val link = param + "/?page=${index}"
        val response = service.getHtml(link)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeyword(html, index)

            return@withContext searchResultList
        }
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): List<SearchResult> {
        val link = param + "/$index"
        val response = service.getHtml(link)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeywordAndGenre(html, index)

            return@withContext searchResultList
        }
    }

    companion object {
        /**
         * Constructs an URL pointing to the website that contains manga of selected genre
         * Example: https://manganato.com/genre-32
         */
        fun getMangakalotGenreUrl(genre: GenreConstants): String {
            val genreId = getMangakalotGenreId(genre)
            return MangakalotService.BASE_URL + "genre-$genreId"
        }

        /**
         * Get the id of genre in Mangakalot's database
         */
        fun getMangakalotGenreId(genre: GenreConstants): Int {
            return when (genre) {
                GenreConstants.ALL -> throw InvalidGenreException("This genre constant does not have its own id")
                GenreConstants.ACTION -> 2
                GenreConstants.ADULT -> 3
                GenreConstants.ADVENTURE -> 4
                GenreConstants.COMEDY -> 6
                GenreConstants.COOKING -> 7
                GenreConstants.DOUJINSHI -> 9
                GenreConstants.DRAMA -> 10
                GenreConstants.ECCHI -> 11
                GenreConstants.FANTASY -> 12
                GenreConstants.GENDER_BENDER -> 13
                GenreConstants.HAREM -> 14
                GenreConstants.HISTORICAL -> 15
                GenreConstants.HORROR -> 16
                GenreConstants.ISEKAI -> 45
                GenreConstants.JOSEI -> 17
                GenreConstants.MANHUA -> 44
                GenreConstants.MANHWA -> 43
                GenreConstants.MARTIAL_ARTS -> 19
                GenreConstants.MATURE -> 20
                GenreConstants.MECHA -> 21
                GenreConstants.MEDICAL -> 22
                GenreConstants.MYSTERY -> 24
                GenreConstants.ONE_SHOT -> 25
                GenreConstants.PSYCHOLOGICAL -> 26
                GenreConstants.ROMANCE -> 27
                GenreConstants.SCHOOL_LIFE -> 28
                GenreConstants.SCIFI -> 29
                GenreConstants.SEINEN -> 30
                GenreConstants.SHOUJO -> 31
                GenreConstants.SHOUJO_AI -> 32
                GenreConstants.SHOUNEN -> 33
                GenreConstants.SHOUNEN_AI -> 34
                GenreConstants.SLICE_OF_LIFE -> 35
                GenreConstants.SMUT -> 36
                GenreConstants.SPORTS -> 37
                GenreConstants.SUPERNATURAL -> 38
                GenreConstants.TRAGEDY -> 39
                GenreConstants.WEBTOONS -> 40
                GenreConstants.YAOI -> 41
                GenreConstants.YURI -> 42
            }
        }
    }

}