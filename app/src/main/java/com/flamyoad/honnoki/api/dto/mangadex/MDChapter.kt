package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDChapter(
    val result: String?,
    val response: String?,
    val data: MDChapterData?
)