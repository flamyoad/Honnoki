package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDBaseUrl(
    val baseUrl: String,
    val chapter: MDChapterImage,
)
