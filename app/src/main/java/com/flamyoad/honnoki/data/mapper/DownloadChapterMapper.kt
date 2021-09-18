package com.flamyoad.honnoki.data.mapper

import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.ui.download.model.DownloadChapter

fun Chapter.mapToDownloadChapter(isSelected: Boolean): DownloadChapter {
    return DownloadChapter(
        id = this.id,
        title = this.title,
        number = this.number,
        date = this.date,
        link = this.link,
        hasBeenRead = this.hasBeenRead,
        hasBeenDownloaded = this.hasBeenDownloaded,
        translatedLanguage = this.translatedLanguage,
        isSelected = isSelected
    )
}

fun List<Chapter>.mapToDownloadChapters(currentlySelectedChapters: List<DownloadChapter>): List<DownloadChapter> {
    return this.map {  dbChapter ->
        val isSelected = currentlySelectedChapters.any { it.id == dbChapter.id }
        dbChapter.mapToDownloadChapter(isSelected)
    }
}