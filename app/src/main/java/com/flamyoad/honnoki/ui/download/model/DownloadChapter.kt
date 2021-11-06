package com.flamyoad.honnoki.ui.download.model

data class DownloadChapter(
    val id: Long?,
    val title: String,
    val number: Double,
    val date: String,
    val link: String,
    val hasBeenRead: Boolean,
    val hasBeenDownloaded: Boolean,
    val translatedLanguage: String,
    val isSelected: Boolean,
    val mangaOverviewId: Long,
)
