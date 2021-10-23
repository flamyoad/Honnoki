package com.flamyoad.honnoki.data.mapper

import androidx.room.ColumnInfo
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
        mangaOverviewId = this.mangaOverviewId,
        isSelected = isSelected,
    )
}

fun List<Chapter>.mapToDownloadChapters(currentlySelectedChapters: List<DownloadChapter>): List<DownloadChapter> {
    return this.map {  dbChapter ->
        val isSelected = currentlySelectedChapters.any { it.id == dbChapter.id }
        dbChapter.mapToDownloadChapter(isSelected)
    }
}

fun DownloadChapter.mapToDb(): Chapter {
    return Chapter(
        id = this.id,
        link = this.link,
        title = this.title,
        number = this.number,
        date = this.date,
        hasBeenRead = this.hasBeenRead,
        hasBeenDownloaded = this.hasBeenDownloaded,
        translatedLanguage = this.translatedLanguage,
        mangaOverviewId = this.mangaOverviewId
    )
}

fun List<DownloadChapter>.mapToDb(): List<Chapter> {
    return this.map { it.mapToDb() }
}