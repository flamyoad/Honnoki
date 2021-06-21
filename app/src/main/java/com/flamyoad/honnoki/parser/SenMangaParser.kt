package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.entities.*
import org.jsoup.Jsoup
import java.time.LocalDateTime

class SenMangaParser {

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
        println("manga size : ${mangaList.size}")
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

        }catch(e: Exception) {
            println(e.stackTrace)
        }

        throw IllegalArgumentException("")
    }

    fun parseForImageList(html: String?): List<Page> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val divs = document.select("script")

        // Senmanga loads the images at runtime with JS code
        val div = document.select("script").toList()
            .filter { it.attrNonNull("type") == "text/javascript" }
            .filter { it.textNonNull().contains("imglist") }
            .firstOrNull()

        val javascript = div!!.text()

        return emptyList()

//        return imageList.mapIndexed { index, element ->
//            Page(
//                link = element.attrNonNull("src"),
//                number = index + 1 // Add 1 to change the value into one-based numbering
//            )
//        }
    }
}