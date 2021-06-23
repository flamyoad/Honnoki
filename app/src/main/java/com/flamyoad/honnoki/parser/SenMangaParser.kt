package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.parser.json.senmanga.SenmangaJsonAdapter
import org.jsoup.Jsoup
import java.io.IOException
import java.lang.NullPointerException
import java.time.LocalDateTime

class SenMangaParser(
    private val jsonAdapter: SenmangaJsonAdapter
) {

    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select("div.item")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst(".series-title").textNonNull()
            val link = div.selectFirst("a").attrNonNull("href")
            val latestChapter = div.selectFirst(".chapter").textNonNull()
            val coverImage = div.selectFirst("img").attrNonNull("src")

            mangaList.add(
                Manga(
                    title = title,
                    link = link,
                    latestChapter = latestChapter,
                    coverImage = coverImage,
                    viewCount = -1, // Non existent in senmanga
                    source = Source.SENMANGA,
                    type = MangaType.RECENTLY
                )
            )
        }
        return mangaList
    }

    fun parseForTrendingMangas(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select("div.item")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst(".series-title").textNonNull()
            val link = div.selectFirst("a").attrNonNull("href")
            val latestChapter = div.selectFirst(".chapter").textNonNull()
            val coverImage = div.selectFirst("img").attrNonNull("src")

            mangaList.add(
                Manga(
                    title = title,
                    link = link,
                    latestChapter = latestChapter,
                    coverImage = coverImage,
                    viewCount = -1, // Non existent in senmanga
                    source = Source.SENMANGA,
                    type = MangaType.TRENDING
                )
            )
        }
        return mangaList
    }

    fun parseForMangaOverview(html: String?, link: String): MangaOverview {
        if (html.isNullOrBlank()) {
            return MangaOverview.empty()
        }

        val document = Jsoup.parse(html)
        val summary = document.selectFirst(".summary")
            .ownTextNonNull()

        val descriptionDiv = document.selectFirst(".series-desc")

        val mainTitle = descriptionDiv.selectFirst(".series").textNonNull()

        val alternativeTitle = descriptionDiv.selectFirst(".alt-name").textNonNull()

        val coverImage = descriptionDiv.selectFirst(".cover > img")
            .attrNonNull("src")

        val status = descriptionDiv.selectFirst("div.info > div:nth-child(2)")
            .ownTextNonNull()

        return MangaOverview(
            id = null,
            coverImage = coverImage,
            mainTitle = mainTitle,
            alternativeTitle = alternativeTitle,
            summary = summary,
            status = status,
            source = Source.SENMANGA,
            link = link,
            lastReadChapterId = -1,
            lastReadDateTime = LocalDateTime.MIN,
            lastReadPageNumber = -1
        )
    }

    fun parseForChapterList(html: String?): List<Chapter> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val chapterList = document.select(".chapter-list > li")
        println("chapter list in senmanga: ${chapterList.size}")

        try {
            val processedChapters = chapterList.mapIndexed { index, it ->
                val chapterDiv = it.selectFirst("li")
                val chapterLink = it.selectFirst("li > a")
                Chapter(
                    title = chapterDiv.selectFirst("a").textNonNull(),
                    number = (chapterList.size - (index + 1)).toDouble(),
                    link = chapterLink.attrNonNull("href"),
                    date = it.selectFirst(".span > .time").attrNonNull("title"),
                    hasBeenRead = false,
                    hasBeenDownloaded = false
                )
            }
            println("size: ${processedChapters.size}")
            return processedChapters

        } catch (e: Exception) {
            println(e.stackTrace)
        }

        throw IllegalArgumentException("")
    }

    fun parseForAuthors(html: String?): List<Author> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val authorDiv = document.select("strong").toList()
            .firstOrNull { it.ownTextNonNull().contains("Author", ignoreCase = true) }
            .parentNonNull()

        val authorLinks = authorDiv.select("a") ?: return emptyList()

        val authors = authorLinks.map {
            return@map Author(
                name = it.textNonNull(),
                link = it.attrNonNull("href")
            )
        }
        return authors
    }

    fun parseForGenres(html: String?): List<Genre> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val genreDiv = document.select("strong").toList()
            .firstOrNull { it.ownTextNonNull().contains("Genre", ignoreCase = true) }
            .parentNonNull()

        val genreLinks = genreDiv.select("a") ?: return emptyList()

        val genres = genreLinks.map {
                return@map Genre(
                    name = it.textNonNull(),
                    link = it.attrNonNull("href")
                )
            }
        return genres
    }

    fun parseForImageList(html: String?): List<Page> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        try {
            val scriptTag = document.select("script").toList()
                .filter { it.attrNonNull("type") == "text/javascript" }
                .firstOrNull { it.htmlNonNull().contains("chapter_url") }

            val javaScript = scriptTag.htmlNonNull()

            val startIndex = javaScript.indexOf("var imglist =")
            val endIndex = javaScript.indexOf(";", startIndex)

            val jsonString = javaScript.substring(startIndex, endIndex)
                .removePrefix("var imglist =")

            val imageJson = jsonAdapter.imageConverter.fromJson(jsonString) ?: emptyList()
            return imageJson.map {
                Page(
                    link = it.url,
                    number = it.id
                )
            }

        } catch (e: Exception) {
            when (e) {
                is NullPointerException -> {
                }// Jsoup can't find the tag
                is StringIndexOutOfBoundsException -> {
                }// Script tag has changed}
                is IOException -> {
                } // JsonAdapter string parsing error
            }
            return emptyList()
        }
    }

    fun parseForSearchByKeyword(html: String?): List<SearchResult> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val searchResult = document.select(".listupd > .upd")

        // Reached end of pagination or error occured
        if (searchResult == null) {
            return emptyList()
        }

        return searchResult.map {
            SearchResult(
                coverImage = it.selectFirst(".cover > img").attrNonNull("src"),
                link = it.selectFirst(".item > a").attrNonNull("href"),
                title = it.selectFirst(".series-title").textNonNull(),
                latestChapter = it.selectFirst(".chapter").textNonNull(),
                author = "" // Non existent
            )
        }
    }

    fun parseForSearchByKeywordAndGenre(html: String?): List<SearchResult> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val searchResult = document.select(".listupd > .upd")

        // Reached end of pagination or error occured
        if (searchResult == null) {
            return emptyList()
        }

        return searchResult.map {
            SearchResult(
                coverImage = it.selectFirst(".cover > img").attrNonNull("src"),
                link = it.selectFirst(".item > a").attrNonNull("href"),
                title = it.selectFirst(".series-title").textNonNull(),
                latestChapter = it.selectFirst(".chapter").textNonNull(),
                author = "" // Non existent
            )
        }
    }
}