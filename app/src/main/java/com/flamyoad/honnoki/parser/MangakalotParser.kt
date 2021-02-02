package com.flamyoad.honnoki.parser

import androidx.core.net.toUri
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.MangaType
import com.flamyoad.honnoki.model.Source
import org.jsoup.Jsoup

class MangakalotParser {

    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select(".list-truyen-item-wrap")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst("h3").text()
            val link = div.selectFirst("h3 > a").attr("href")
            val latestChapter = div.selectFirst(".list-story-item-wrap-chapter").text()
            val coverImage = div.selectFirst("img").attr("src")
            val viewCount = div.selectFirst("span").text()

            mangaList.add(Manga(
                title = title,
                link = link,
                latestChapter = latestChapter,
                coverImage = coverImage,
                viewCount = viewCount.toIntOrNull() ?: 0,
                source = Source.MANGAKALOT,
                type = MangaType.RECENTLY
            ))
        }

        return mangaList
    }

    fun parseForTrendingManga(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select(".list-truyen-item-wrap")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst("h3").text()
            val link = div.selectFirst("h3 > a").attr("href")
            val latestChapter = div.selectFirst(".list-story-item-wrap-chapter").text()
            val coverImage = div.selectFirst("img").attr("src")
            val viewCount = div.selectFirst("span").text()

            mangaList.add(Manga(
                title = title,
                link = link,
                latestChapter = latestChapter,
                coverImage = coverImage,
                viewCount = viewCount.toIntOrNull() ?: 0,
                source = Source.MANGAKALOT,
                type = MangaType.TRENDING
            ))
        }

        return mangaList
    }
}