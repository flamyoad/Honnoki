package com.flamyoad.honnoki.api.dto.mangadex.relationships

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RelUser(
    @Json(name = "id") override val id: String,
    @Json(name = "type") override val type: String,
): BaseRelationship
