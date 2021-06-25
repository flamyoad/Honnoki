package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.api.dto.mangadex.MDResult
import com.flamyoad.honnoki.api.dto.mangadex.MDResultList
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelArtist
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelAuthor
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelCoverImage
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.parser.exception.NullMangaIdException
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

        val artistAttr = json.relationships
            ?.filter { rel -> rel.type == "artist" }
            ?.map { it as? RelArtist }

        val authorAttr = json.relationships
            ?.filter { rel -> rel.type == "author" }
            ?.map { it as? RelAuthor }

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

    private enum class CoverImageQuality {
        BEST,
        WIDTH_512PX,
        WIDTH_256PX
    }
}