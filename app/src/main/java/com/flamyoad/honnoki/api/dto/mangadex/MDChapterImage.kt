package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDChapterImage(
    val hash: String?,
    val data: List<String>,
    val dataSaver: List<String>,
)
