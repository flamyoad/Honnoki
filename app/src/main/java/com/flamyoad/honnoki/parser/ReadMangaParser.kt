package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.source.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.LocalDateTime

class ReadMangaParser {
    suspend fun parseForRecentMangas(html: String?): List<Manga> =
        withContext(Dispatchers.Default) {
            if (html == null) return@withContext emptyList()

            val document = Jsoup.parse(html)
            val mangaDivs = document.select(".style-thumbnail > .clearfix > li")
                ?: return@withContext emptyList()

            val mangaList = mutableListOf<Manga>()
            for (div in mangaDivs) {
                val title = div.selectFirst("a").attrNonNull("title")
                val coverImage = div.selectFirst("img").attrNonNull("src")
                val link = div.selectFirst("a").attrNonNull("href")

                mangaList.add(
                    Manga(
                        title = title,
                        link = link,
                        latestChapter = "", // Non existent
                        coverImage = coverImage,
                        viewCount = -1, // Non existent
                        source = Source.READMANGA,
                        type = MangaType.RECENTLY
                    )
                )
            }
            return@withContext mangaList
        }

    suspend fun parseForTrendingMangas(html: String?): List<Manga> =
        withContext(Dispatchers.Default) {
            if (html == null) return@withContext emptyList()

            val document = Jsoup.parse(html)
            val mangaDivs = document.select(".style-thumbnail > .clearfix > li")
                ?: return@withContext emptyList()

            val mangaList = mutableListOf<Manga>()
            for (div in mangaDivs) {
                val title = div.selectFirst("a").attrNonNull("title")
                val coverImage = div.selectFirst("img").attrNonNull("src")
                val link = div.selectFirst("a").attrNonNull("href")

                mangaList.add(
                    Manga(
                        title = title,
                        link = link,
                        latestChapter = "", // Non existent
                        coverImage = coverImage,
                        viewCount = -1, // Non existent
                        source = Source.READMANGA,
                        type = MangaType.TRENDING
                    )
                )
            }
            return@withContext mangaList
        }

    suspend fun parseForMangaOverview(html: String?, link: String): MangaOverview =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext MangaOverview.empty()
            }

            val document = Jsoup.parse(html)

            val contentDiv = document.selectFirst(".panel-primary")
                ?: return@withContext MangaOverview.empty()

            val mainTitle = contentDiv.selectFirst(".panel-heading > h1").textNonNull()

            val summary = contentDiv.selectFirst(".movie-detail > p").textNonNull()

            val coverImage = contentDiv.selectFirst("img").attrNonNull("src")

            val alternativeTitle = contentDiv.selectFirst("dl > dd:nth-child(2)").ownTextNonNull()

            val status = contentDiv.selectFirst("dl > dd:nth-child(4)").ownTextNonNull()

            return@withContext MangaOverview(
                id = null,
                coverImage = coverImage,
                mainTitle = mainTitle,
                alternativeTitle = alternativeTitle,
                summary = summary,
                status = status,
                source = Source.READMANGA,
                link = link,
                lastReadChapterId = -1,
                lastReadDateTime = LocalDateTime.MIN,
                lastReadPageNumber = -1
            )
        }

    suspend fun parseForGenres(html: String?): List<Genre> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)
            val genreList = document.selectFirst("dl > dd:nth-child(6)")
                ?: return@withContext emptyList()

            return@withContext genreList
                .select("a")
                .mapNotNull { Genre(name = it.textNonNull(), link = it.attrNonNull("href")) }
        }

    suspend fun parseForAuthors(html: String?): List<Author> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val authorDiv = document.selectFirst(".director")
                ?: return@withContext emptyList()
            val authorLink = authorDiv.selectFirst("a").attrNonNull("href")
            val authorName = authorDiv.selectFirst("ul > li:nth-child(1)").text()

            return@withContext listOf(
                Author(name = authorName, link = authorLink)
            )
        }

    suspend fun parseForChapterList(html: String?): List<Chapter> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val chapterList = document.select(".chp_lst > li")
                ?: return@withContext emptyList()

            return@withContext chapterList.mapIndexed { index, it ->
                val link = it.selectFirst("a").attrNonNull("href") + "/all-pages"
                Chapter(
                    title = it.selectFirst(".val").textNonNull(),
                    number = (chapterList.size - (index + 1)).toDouble(),
                    link = link,
                    date = it.selectFirst(".dte").attrNonNull("title"),
                    hasBeenRead = false,
                    hasBeenDownloaded = false
                )
            }
        }

    suspend fun parseForPageList(html: String?): List<Page> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val imageList = document.select(".content-list > img")
                ?: return@withContext emptyList()

            val pages = imageList.mapIndexed { index, element ->
                Page(number = index + 1, link = element.attrNonNull("src"))
            }
            return@withContext pages
        }

    suspend fun parseForSearchByKeyword(html: String?): List<SearchResult> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val searchResult = document.select(".box") ?: return@withContext emptyList()
            return@withContext searchResult.map {
                SearchResult(
                    coverImage = it.selectFirst(".left > a > img").attrNonNull("src"),
                    link = it.selectFirst(".left > a").attrNonNull("href"),
                    title = it.selectFirst(".title > h2").textNonNull(),
                    latestChapter = it.selectFirst("dd:nth-child(2)").textNonNull(), // Status
                    author = it.selectFirst("dd:nth-child(4)").textNonNull() // Genre
                )
            }
        }

    suspend fun parseForSearchByAuthor(html: String?): List<SearchResult> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val searchResult = document.select(".movies-grid > ul > li") ?: emptyList()
            return@withContext searchResult.map {
                SearchResult(
                    coverImage = it.selectFirst("a.thumbnail > img").attrNonNull("src"),
                    link = it.selectFirst(".movie-title").attrNonNull("href"),
                    title = it.selectFirst(".movie-title").textNonNull(),
                    latestChapter = "",
                    author = "",
                )
            }
        }
}