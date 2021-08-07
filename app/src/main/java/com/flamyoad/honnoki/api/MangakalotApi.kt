package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.exception.InvalidGenreException
import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.parser.MangakalotParser
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.utils.extensions.stringSuspending

class MangakalotApi(
    private val service: MangakalotService,
    private val parser: MangakalotParser,
    apiHandler: ApiRequestHandler
) : BaseApi(apiHandler) {

    override val startingPageIndex: Int
        get() = 1

    override suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getLatestManga(index) },
            parseData = { parser.parseForRecentMangas(it.stringSuspending()) }
        )
    }

    override suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getTrendingManga(index) },
            parseData = { parser.parseForTrendingManga(it.stringSuspending()) }
        )
    }

    override suspend fun searchForTopManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getTopWeekManga() },
            parseData = { parser.parseForTopManga(it.stringSuspending()) }
        )
    }

    override suspend fun searchForNewManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getNewManga(index) },
            parseData = { parser.parseForNewManga(it.stringSuspending()) }
        )
    }

    suspend fun searchForMangaOverview(link: String): State<MangaOverview> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForMangaOverview(it.stringSuspending(), link) }
        )
    }

    suspend fun searchForGenres(link: String): State<List<Genre>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForGenres(it.stringSuspending()) }
        )
    }

    suspend fun searchForAuthors(link: String): State<List<Author>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForAuthors(it.stringSuspending()) }
        )
    }

    suspend fun searchForChapterList(link: String): State<List<Chapter>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForChapterList(it.stringSuspending()) }
        )
    }

    suspend fun searchForImageList(link: String): State<List<Page>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForImageList(it.stringSuspending()) }
        )
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): State<List<SearchResult>> {
        return processApiData(
            apiCall = { service.searchByKeyword(keyword, index) },
            parseData = { parser.parseForSearchByKeyword(it.stringSuspending(), index) }
        )
    }

    override suspend fun searchByKeywordAndGenres(
        keyword: String,
        genre: GenreConstants,
        index: Int
    ): State<List<SearchResult>> {
        if (genre == GenreConstants.ALL) {
            throw InvalidGenreException("There is no id for `All` genres! Use searchByKeyword() instead of searchByKeywordAndGenres()")
        }

        val genreString = "_" + getMangakalotGenreId(genre) + "_"
        return processApiData(
            apiCall = {
                service.searchByKeywordAndGenres(
                    genre = genreString,
                    keyword = keyword,
                    index = index
                )
            },
            parseData = { parser.parseForSearchByKeywordAndGenre(it.stringSuspending(), index) }
        )
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "/?page=${index}"
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForSearchByKeyword(it.stringSuspending(), index) }
        )
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "/$index"
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForSearchByKeywordAndGenre(it.stringSuspending(), index) }
        )
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