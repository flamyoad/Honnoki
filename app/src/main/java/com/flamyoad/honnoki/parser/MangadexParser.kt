package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.api.dto.mangadex.*
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelArtist
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelAuthor
import com.flamyoad.honnoki.api.dto.mangadex.relationships.RelCoverImage
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.utils.extensions.capitalizeWithLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class MangadexParser {

    suspend fun parseHomeMangas(json: MDResultList, type: MangaType): List<Manga> =
        withContext(Dispatchers.Default) {
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
            return@withContext mangas ?: emptyList()
        }

    suspend fun parseForMangaOverview(json: MDResult): MangaOverview =
        withContext(Dispatchers.Default) {
            val mangaId = json.data?.id ?: return@withContext MangaOverview.empty()

            val attributes = json.data.attributes

            val coverImageAttr =
                json.relationships?.firstOrNull { rel -> rel.type == "cover_art" } as? RelCoverImage

            val coverImage = constructCoverImageUrl(
                mangaId,
                coverImageAttr?.getFileName() ?: "",
                CoverImageQuality.WIDTH_512PX
            )

            val alternativeTitle = attributes?.altTitles?.joinToString {
                it?.en ?: ""
            } ?: ""

            val summary = attributes?.description?.en ?: ""

            val status = attributes?.status?.capitalizeWithLocale() ?: ""

            return@withContext MangaOverview(
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

    suspend fun parseForAuthors(json: MDResult): List<Author> =
        withContext(Dispatchers.Default) {
            if (json.data?.id == null) return@withContext emptyList()

            val artistAttr = json.relationships
                ?.filter { rel -> rel.type == "artist" }
                ?.map { it as? RelArtist } ?: emptyList()

            val authorAttr = json.relationships
                ?.filter { rel -> rel.type == "author" }
                ?.map { it as? RelAuthor } ?: emptyList()

            val artists = artistAttr
                .filter { it?.attributes?.name?.isNotBlank() == true }
                .map {
                    Author(
                        name = it?.attributes?.name ?: "",
                        link = it?.id ?: ""
                    )
                }

            val authors = authorAttr
                .filter { it?.attributes?.name?.isNotBlank() == true } // Sometimes MangaDex returns empty value. Eg. https://mangadex.org/title/388f639e-7cd1-4493-8242-ae480647479a
                .map {
                    Author(
                        name = it?.attributes?.name ?: "",
                        link = it?.id ?: ""
                    )
                }

            return@withContext (artists + authors)
        }

    suspend fun parseForGenres(json: MDResult): List<Genre> =
        withContext(Dispatchers.Default) {
            if (json.data?.id == null) return@withContext emptyList()

            val attributes = json.data.attributes

            val genresData = attributes?.tags
                ?.filter { it.attributes?.group == "genre" }
                ?.map { it }
                ?: emptyList()

            return@withContext genresData.map {
                Genre(
                    name = it.attributes?.name?.en ?: "",
                    link = it.id ?: ""
                )
            }
        }

    suspend fun parseForChapters(json: MDChapter, currentOffset: Int): List<Chapter> =
        withContext(Dispatchers.Default) {
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

                val date = try {
                    OffsetDateTime
                        .parse(attr.createdAt)
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME)
                } catch (dte: DateTimeException) {
                    attr.createdAt
                }

                val chapterNumber = attr.chapter?.toDoubleOrNull()
                    ?: (index + currentOffset).toDouble()

                Chapter(
                    title = title,
                    number = chapterNumber,
                    link = it.data.id,
                    date = date ?: "",
                    hasBeenRead = false,
                    hasBeenDownloaded = false,
                    translatedLanguage = attr.translatedLanguage ?: ""
                )
            }
            return@withContext chapterList
        }

    suspend fun parseForImageList(
        chapterJson: MDChapterResult,
        baseUrlJson: MDBaseUrl
    ): List<Page> =
        withContext(Dispatchers.Default) {
            val baseUrl = baseUrlJson.baseUrl
            val chapter = chapterJson.data
            val chapterHash = chapter.attributes.hash ?: ""

            return@withContext chapter.attributes.data.mapIndexed { index, fileName ->
                Page(
                    number = index + 1,
                    link = constructPageUrl(
                        baseUrl,
                        MangadexQualityMode.DATA,
                        chapterHash,
                        fileName
                    ),
                    linkDataSaver = constructPageUrl(
                        baseUrl,
                        MangadexQualityMode.DATA_SAVER,
                        chapterHash,
                        fileName
                    )
                )
            }
        }

    suspend fun parseForSearchResult(json: MDResultList): List<SearchResult> {
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