package com.flamyoad.honnoki.parser.json.senmanga

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SenmangaImageJson(
    @Json(name="id") val id: Int,
    @Json(name="file_name") val fileName: String,
    @Json(name="url") val url: String,
    @Json(name="type") val type: String
)