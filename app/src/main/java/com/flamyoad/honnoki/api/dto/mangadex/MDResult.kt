package com.flamyoad.honnoki.api.dto.mangadex

import com.flamyoad.honnoki.api.dto.mangadex.relationships.BaseRelationship
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDResult(
    @Json(name = "result") val result: String?,
    @Json(name = "data") val data: MDResultData?,
    @Json(name = "relationships") val relationships: List<BaseRelationship>?,
)

@JsonClass(generateAdapter = true)
data class MDResultData(
    @Json(name = "id") val id: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "attributes") val attributes: MDResultDataAttributes?
)

@JsonClass(generateAdapter = true)
data class MDResultDataAttributes(
    @Json(name = "title") val title: MDTitle?,
    @Json(name = "altTitles") val altTitles: List<MDTitle>?,
    @Json(name = "description") val description: MDDescription,
    @Json(name = "links") val links: MDLinks?,
    @Json(name = "originalLanguage") val originalLanguage: String?,
    @Json(name = "lastVolume") val lastVolume: String?,
    @Json(name = "lastChapter") val lastChapter: String?,
    @Json(name = "publicationDemographic") val publicationDemographic: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "year") val year: String?,
    @Json(name = "contentRating") val contentRating: String?,
    @Json(name = "tags") val tags: List<MDTag>?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?,
    @Json(name = "version") val version: Int?
)

@JsonClass(generateAdapter = true)
data class MDTitle(
    @Json(name = "en") val en: String?
)

@JsonClass(generateAdapter = true)
data class MDDescription(
    @Json(name = "en") val en: String?
)

@JsonClass(generateAdapter = true)
data class MDLinks(
    @Json(name = "property1") val property1: String?,
    @Json(name = "property2") val property2: String?
)

@JsonClass(generateAdapter = true)
data class MDTag(
    @Json(name = "id") val id: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "attributes") val attributes: MDTagAttribute?
)

@JsonClass(generateAdapter = true)
data class MDTagAttribute(
    @Json(name = "name") val name: MDTagName?,
    @Json(name = "description") val description: List<String>?,
    @Json(name = "group") val group: String?,
    @Json(name = "version") val version: Int?
)

@JsonClass(generateAdapter = true)
data class MDTagName(
    @Json(name = "en") val en: String?
)


