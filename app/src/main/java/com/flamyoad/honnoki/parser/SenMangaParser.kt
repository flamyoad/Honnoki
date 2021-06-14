package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.MangaOverview
import com.flamyoad.honnoki.data.model.MangaType
import com.flamyoad.honnoki.data.model.Source
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
            source = Source.MANGAKALOT,
            link = link,
            lastReadChapterId = -1,
            lastReadDateTime = LocalDateTime.MIN,
            lastReadPageNumber = -1
        )
    }
}