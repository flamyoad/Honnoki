package com.flamyoad.honnoki.ui.overview.model

data class ReaderChapter(
    val id: Long?,
    val title: String,
    val number: Double,
    val date: String,
    val link: String,
    val currentlyRead: Boolean,
    val hasBeenRead: Boolean,
    val hasBeenDownloaded: Boolean,
    val translatedLanguage: String,
    val mangaOverviewId: Long = -1
)