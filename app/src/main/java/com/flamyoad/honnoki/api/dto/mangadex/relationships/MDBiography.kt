package com.flamyoad.honnoki.api.dto.mangadex.relationships

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDBiography(
    @Json(name="en") val en: String?
)
