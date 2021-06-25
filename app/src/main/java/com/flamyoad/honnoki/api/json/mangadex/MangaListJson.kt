package com.flamyoad.honnoki.api.json.mangadex

import com.flamyoad.honnoki.api.json.mangadex.relationships.BaseRelationship
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MangaListJson(
    @Json(name = "results") val results: List<MangaListResults>?,
    @Json(name = "limit") val limit: Int,
    @Json(name = "offset") val offset: Int,
    @Json(name = "total") val total: Int
)

@JsonClass(generateAdapter = true)
data class MangaListResults(
    @Json(name = "result") val result: String?,
    @Json(name = "data") val data: MangaListData?,
    @Json(name = "relationships") val relationships: List<BaseRelationship>?,
)

@JsonClass(generateAdapter = true)
data class MangaListData(
    @Json(name = "id") val id: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "attributes") val attributes: MangaListDataAttributes?
)

@JsonClass(generateAdapter = true)
data class MangaListDataAttributes(
    @Json(name = "title") val title: MangaListTitle?,
    @Json(name = "altTitles") val altTitles: List<MangaListTitle>?,
    @Json(name = "links") val links: MangaListLinks?,
    @Json(name = "originalLanguage") val originalLanguage: String?,
    @Json(name = "lastVolume") val lastVolume: String?,
    @Json(name = "lastChapter") val lastChapter: String?,
    @Json(name = "publicationDemographic") val publicationDemographic: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "year") val year: String?,
    @Json(name = "contentRating") val contentRating: String?,
    @Json(name = "tags") val tags: List<MangaListTag>?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?,
    @Json(name = "version") val version: Int?
)

@JsonClass(generateAdapter = true)
data class MangaListTitle(
    @Json(name = "en") val en: String?
)

@JsonClass(generateAdapter = true)
data class MangaListTag(
    @Json(name = "id") val id: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "attributes") val attributes: MangaListTagAttributes?
)

@JsonClass(generateAdapter = true)
data class MangaListTagAttributes(
    @Json(name = "name") val name: MangaListTagName?,
    @Json(name = "description") val description: List<String>?,
    @Json(name = "group") val group: String?,
    @Json(name = "version") val version: Int?
)

@JsonClass(generateAdapter = true)
data class MangaListTagName(
    @Json(name = "en") val en: String?
)

@JsonClass(generateAdapter = true)
data class MangaListLinks(
    @Json(name = "property1") val property1: String?,
    @Json(name = "property2") val property2: String?
)

