package com.flamyoad.honnoki.data.mapper

import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.ui.overview.model.ReaderChapter

fun Chapter.mapToDomain(mangaOverview: MangaOverview): ReaderChapter {
    return ReaderChapter(
        id = this.id,
        title = this.title,
        number = this.number,
        date = this.date,
        link = this.link,
        currentlyRead = (this.id == mangaOverview.lastReadChapterId),
        hasBeenRead = this.hasBeenRead,
        hasBeenDownloaded = this.hasBeenDownloaded,
        mangaOverviewId = mangaOverview.id ?: -1L
    )
}

fun List<Chapter>.mapToDomain(mangaOverview: MangaOverview): List<ReaderChapter> {
    return this.map {  dbChapter ->
        dbChapter.mapToDomain(mangaOverview)
    }
}

fun ReaderChapter.mapToDb(): Chapter {
    return Chapter(
        id = this.id,
        title = this.title,
        number = this.number,
        date = this.date,
        link = this.link,
        hasBeenRead = this.hasBeenRead,
        hasBeenDownloaded = this.hasBeenDownloaded,
        mangaOverviewId = this.mangaOverviewId
    )
}

fun List<ReaderChapter>.mapToDb(): List<Chapter> {
    return this.map { domainChapter ->
        domainChapter.mapToDb()
    }
}