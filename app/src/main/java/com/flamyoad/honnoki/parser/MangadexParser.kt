package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.api.dto.mangadex.*
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelArtist
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelAuthor
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelCoverImage
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.parser.exception.NullMangaIdException
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.utils.extensions.capitalizeWithLocale
import java.time.LocalDateTime

class MangadexParser {

    fun parseHomeMangas(json: MDResultList, type: MangaType): List<Manga> {
        val mangas = json.results?.map { it ->
            val mangaId = it.data?.id ?: ""

            /*  If it is null (cannot be casted to RelCoverImage),
             *  means it does not have cover image... Lol ask MangaDex
             */
            val coverImage = it.relationships?.firstOrNull { rel -> rel.type == "cover_art" }
            val coverImageUrl = if (coverImage is RelCoverImage) {
                constructCoverImageUrl(
                    mangaId,
                    coverImage.getFileName(),
                    CoverImageQuality.WIDTH_512PX
                )
            } else {
                ""
            }

            Manga(
                title = it.data?.attributes?.title?.en ?: "",
                link = mangaId,
                latestChapter = "",
                coverImage = coverImageUrl,
                viewCount = 0, // Don't care
                source = Source.MANGADEX,
                type = type
            )
        }
        return mangas ?: emptyList()
    }

    fun parseForMangaOverview(json: MDResult): MangaOverview {
        val mangaId = json.data?.id ?: throw NullMangaIdException()

        val attributes = json.data.attributes

        val coverImageAttr =
            json.relationships?.firstOrNull { rel -> rel.type == "cover_art" } as? RelCoverImage

        val coverImage = constructCoverImageUrl(
            mangaId,
            coverImageAttr?.getFileName() ?: "",
            CoverImageQuality.WIDTH_512PX
        )

        val alternativeTitle = attributes?.altTitles?.joinToString {
            it.en ?: ""
        } ?: ""

        val summary = attributes?.description?.en ?: ""

        val status = attributes?.status?.capitalizeWithLocale() ?: ""

        return MangaOverview(
            id = null,
            coverImage = coverImage,
            mainTitle = attributes?.title?.en ?: "",
            alternativeTitle = alternativeTitle,
            summary = summary,
            status = status,
            source = Source.MANGADEX,
            link = mangaId,
            lastReadChapterId = -1,
            lastReadDateTime = LocalDateTime.MIN,
            lastReadPageNumber = -1
        )
    }

    fun parseForAuthors(json: MDResult): List<Author> {
        if (json.data?.id == null) throw NullMangaIdException()

        val artistAttr = json.relationships
            ?.filter { rel -> rel.type == "artist" }
            ?.map { it as? RelArtist } ?: emptyList()

        val authorAttr = json.relationships
            ?.filter { rel -> rel.type == "author" }
            ?.map { it as? RelAuthor } ?: emptyList()

        val artists = artistAttr.map {
            Author(
                name = it?.attributes?.name ?: "",
                link = it?.id ?: ""
            )
        }

        val authors = authorAttr.map {
            Author(
                name = it?.attributes?.name ?: "",
                link = it?.id ?: ""
            )
        }

        return (artists + authors)
    }

    fun parseForGenres(json: MDResult): List<Genre> {
        if (json.data?.id == null) throw NullMangaIdException()

        val attributes = json.data.attributes

        val genresData = attributes?.tags
            ?.filter { it.attributes?.group == "genre" }
            ?.map { it }
            ?: emptyList()

        return genresData.map {
            Genre(
                name = it.attributes?.name?.en ?: "",
                link = it.id ?: ""
            )
        }
    }

    fun parseForChapters(json: MDChapter, currentOffset: Int): List<Chapter> {
        val chapterList = json.results.mapIndexed { index, it ->
            val attr = it.data.attributes

            // We don't use the `title` field from JSON since it could be empty.
            // so just substitute it with "Vol. X Ch. X" string
            val title = StringBuilder()
                .append("Vol. ")
                .append(attr.volume ?: "")
                .append(" Ch. ")
                .append(attr.chapter ?: "")
                .toString()

            Chapter(
                title = title,
                number = (index + currentOffset).toDouble(),
                link = it.data.id,
                date = attr.createdAt ?: "",
                hasBeenRead = false,
                hasBeenDownloaded = false
            )
        }
        return chapterList
    }

    fun parseForImageList(chapterJson: MDChapterResult, baseUrlJson: MDBaseUrl): List<Page> {
        val baseUrl = baseUrlJson.baseUrl
        val chapter = chapterJson.data
        val chapterHash = chapter.attributes.hash ?: ""

        return chapter.attributes.data.mapIndexed { index, fileName ->
            Page(
                number = index + 1,
                link = constructPageUrl(baseUrl, MangadexQualityMode.DATA, chapterHash, fileName)
            )
        }
    }

    fun parseForSearchResult(json: MDResultList): List<SearchResult> {
        val searchResult = json.results?.map { it ->
            val mangaId = it.data?.id ?: ""

            /*  If it is null (cannot be casted to RelCoverImage),
             *  means it does not have cover image... Lol ask MangaDex
             */
            val coverImage = it.relationships?.firstOrNull { rel -> rel.type == "cover_art" }
            val coverImageUrl = if (coverImage is RelCoverImage) {
                constructCoverImageUrl(
                    mangaId,
                    coverImage.getFileName(),
                    CoverImageQuality.WIDTH_512PX
                )
            } else {
                ""
            }

            val authors = parseForAuthors(it).joinToString(", ") { it.name }

            SearchResult(
                coverImage = coverImageUrl,
                link = it.data?.id ?: "",
                title = it.data?.attributes?.title?.en ?: "",
                latestChapter = "",
                author = authors,
            )
        }
        return searchResult ?: emptyList()
    }

    private fun constructCoverImageUrl(
        mangaId: String,
        fileName: String,
        quality: CoverImageQuality
    ): String {
        return when (quality) {
            CoverImageQuality.BEST -> "https://uploads.mangadex.org/covers/${mangaId}/${fileName}.jpg"
            CoverImageQuality.WIDTH_512PX -> "https://uploads.mangadex.org/covers/${mangaId}/${fileName}.512.jpg"
            CoverImageQuality.WIDTH_256PX -> "https://uploads.mangadex.org/covers/${mangaId}/${fileName}.256.jpg"
        }
    }

    /**
     * https://api.mangadex.org/docs.html#section/Reading-a-chapter-using-the-API/Retrieving-pages-from-the-MangaDex@Home-network
     */
    private fun constructPageUrl(
        baseUrl: String,
        qualityMode: MangadexQualityMode,
        chapterHash: String,
        fileName: String
    ): String {
        return "$baseUrl/${qualityMode.value}/$chapterHash/$fileName"
    }

    private enum class CoverImageQuality {
        BEST,
        WIDTH_512PX,
        WIDTH_256PX
    }
}