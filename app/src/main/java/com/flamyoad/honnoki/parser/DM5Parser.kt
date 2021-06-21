package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.DM5Service
import com.flamyoad.honnoki.parser.json.dm5.DM5Deobfuscator
import com.flamyoad.honnoki.parser.json.dm5.DM5JsonAdapter
import org.jsoup.Jsoup
import java.time.LocalDateTime

// Webtoon https://www.dm5.com/manhua-miaoshouxiaocunyi/
// Manga https://www.dm5.com/manhua-nvpengyou-jiewoyixia/
class DM5Parser(
    private val jsonAdapter: DM5JsonAdapter,
    private val deObfuscator: DM5Deobfuscator
) {

    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html.isNullOrBlank())
            return emptyList()

        val jsonModel = jsonAdapter.recentMangaConverter.fromJson(html) ?: return emptyList()

        return jsonModel.items.map {
            Manga(
                title = it.title,
                link = DM5Service.BASE_URL + it.urlKey,
                latestChapter = it.showLastPartName,
                coverImage = it.showPicUrlB,
                viewCount = 0, // Don't care
                source = Source.DM5,
                type = MangaType.RECENTLY
            )
        }
    }

    // Weekly trending
    fun parseForTrendingManga(html: String?): List<Manga> {
        val document = Jsoup.parse(html)
        val mangaDivs =
            document.select("body > section.box.container.pb40.overflow-Show.js_top_container > div > ul:nth-child(2) > li")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst(".title").textNonNull()

            val relativeLink = div.selectFirst(".title > a")
                .attrNonNull("href")
                .removePrefix("/")

            val absoluteLink = DM5Service.BASE_URL + relativeLink
            
            val coverImage = div.selectFirst("div.mh-tip-wrap > div > a > p")
                .attrNonNull("style")
                .removePrefix("background-image: url(")
                .removeSuffix(")")

            val latestChapter = div.selectFirst(".chapter > a").ownTextNonNull()

            mangaList.add(
                Manga(
                    title = title,
                    link = absoluteLink,
                    latestChapter = latestChapter,
                    coverImage = coverImage,
                    viewCount = 0,
                    source = Source.DM5,
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

        val div = document.selectFirst(".banner_detail_form")

        val summary = div.selectFirst(".content")
            .ownTextNonNull()

        val coverImage = div.selectFirst(".cover > img")
            .attrNonNull("src")

        val mainTitle = div.selectFirst(".title")
            .ownTextNonNull()
            .trim()

        val status = div.selectFirst(".tip > span:nth-child(1) > span")
            .textNonNull()

        return MangaOverview(
            id = null,
            coverImage = coverImage,
            mainTitle = mainTitle,
            alternativeTitle = "", // None,
            summary = summary,
            status = status,
            source = Source.DM5,
            link = link,
            lastReadChapterId = -1,
            lastReadDateTime = LocalDateTime.MIN,
            lastReadPageNumber = -1
        )
    }

    fun parseForAuthors(html: String?): List<Author> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val authorLinks = document.selectFirst("div.banner_detail_form > div.info > p.subtitle")
            ?: return emptyList()

        val authors = authorLinks.select("a")
            .map {
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

        // If the HTML tag does not exist at all, just return empty list
        val genreLinks =
            document.selectFirst("div.banner_detail_form > div.info > p.tip > span:nth-child(2)")
                ?: return emptyList()

        val genres = genreLinks.select("a")
            .map {
                return@map Genre(
                    name = it.textNonNull(),
                    link = DM5Service.BASE_URL + it.attrNonNull("href")
                )
            }

        return genres
    }

    fun parseForChapterList(html: String?): List<Chapter> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        // The first  `ul` contains the list of chapters visible (normally 20~30)
        // The second `ul` contains the chapters that are expandable/collapsible (style="display:none")
        // Hence, we flatmap both of them into same list
        val chapterList = document
            .select("#chapterlistload > ul")
            .flatMap { it.select("li") }

        return chapterList.mapIndexed { index, div ->
            val chapterLink = div.selectFirst("a")

            // Fallback to webtoon title, if HTML tag for manga title is empty
            val chapterTitle = chapterLink.ownTextNonNull().let {
                if (it.isBlank()) {
                    div.selectFirst(".title").ownTextNonNull()
                } else {
                    it
                }
            }

            Chapter(
                title = chapterTitle,
                number = (chapterList.size - (index + 1)).toDouble(),
                link = chapterLink.attrNonNull("href"), // It's relative link
                date = "", // Does not have date
                hasBeenRead = false,
                hasBeenDownloaded = false
            )
        }
    }

    fun parseForImageList(html: String?): List<Page> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val obfuscatedJS = document.select("script").toList()
            .filter { it.attrNonNull("type") == "text/javascript" }
            .firstOrNull { it.html().contains("eval") }
            .htmlNonNull()

        val imageLinks = deObfuscator.getChapterImagesFromJs(obfuscatedJS)

        val pages = imageLinks.mapIndexed { index, s ->
            Page(number = index + 1, link = s)
        }
        return pages
    }

    fun parseForSearchByKeyword(html: String?, index: Int): List<SearchResult> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        // Check if there is item in the main banner (If none at first page, means no result)
        // If at second page onwards, do nothing because it only appears at 1st page
        if (index == 1 && document.selectFirst(".banner_detail_form") == null) {
            return emptyList()
        }

        // Reached end of pagination
        if (document.selectFirst(".box404") != null) {
            return emptyList()
        }

        val firstItem = document.select(".banner_detail_form").map {
            val authors = it.select(".subtitle > a")
                .map { it.ownTextNonNull() }
                .joinToString()

            SearchResult(
                coverImage = it.selectFirst(".cover > img").attrNonNull("src"),
                link = it.selectFirst(".title > a").attrNonNull("href"),
                title = it.selectFirst(".title > a").textNonNull(),
                latestChapter = it.selectFirst(".chapter > a").ownTextNonNull(),
                author = authors,
            )
        }

        val itemList = document.select(".mh-list > li").map {
            val coverImage = it.selectFirst(".mh-cover").attrNonNull("style")
                .removePrefix("background-image: url(")
                .removeSuffix(")")

            val link = DM5Service.BASE_URL +
                    it.selectFirst(".title > a").attrNonNull("href").removePrefix("/")

            SearchResult(
                coverImage = coverImage,
                link = link,
                title = it.selectFirst(".title > a").textNonNull(),
                latestChapter = it.selectFirst(".chapter > a").ownTextNonNull(),
                author = it.selectFirst(".author > span > a").ownTextNonNull(),
            )
        }

        return (firstItem + itemList)
    }
}