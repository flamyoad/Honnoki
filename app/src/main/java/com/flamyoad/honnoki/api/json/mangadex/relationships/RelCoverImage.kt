package com.flamyoad.honnoki.api.json.mangadex.relationships

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RelCoverImage(
    @Json(name = "id") val id: String,
    @Json(name = "type") override val type: String,
    @Json(name = "attributes") val attributes: RelCoverImageAttr?
) : BaseRelationship {

    /**
     * Gets the file name of the cover image. Returns empty string if null
     */
    fun getFileName(): String {
        return attributes?.fileName ?: ""
    }
}

@JsonClass(generateAdapter = true)
data class RelCoverImageAttr(
    @Json(name = "description") val description: String?,
    @Json(name = "volume") val volume: String?,
    @Json(name = "fileName") val fileName: String?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?,
    @Json(name = "version") val version: Int?
)
