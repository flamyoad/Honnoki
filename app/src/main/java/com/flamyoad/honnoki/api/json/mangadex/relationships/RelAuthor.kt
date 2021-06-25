package com.flamyoad.honnoki.api.json.mangadex.relationships

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RelAuthor(
    @Json(name = "id") override val id: String,
    @Json(name = "type") override val type: String,
    @Json(name = "attributes") val attributes: RelArtistAttr?
): BaseRelationship

@JsonClass(generateAdapter = true)
data class RelAuthorAttr(
    @Json(name = "name") val name : String?,
    @Json(name = "imageUrl") val imageUrl : String?,
    @Json(name = "biography") val biography : List<String>,
    @Json(name = "createdAt") val createdAt : String?,
    @Json(name = "updatedAt") val updatedAt : String?,
    @Json(name = "version") val version : Int?
)
