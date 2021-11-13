package com.flamyoad.honnoki.ui.reader.model

import com.flamyoad.honnoki.data.entities.PageWithChapterInfo

sealed class ReaderPage(val id: Long) {
    data class Value(val pageWithChapterInfo: PageWithChapterInfo) :
        ReaderPage(pageWithChapterInfo.page.id ?: -1) {
        val page get() = pageWithChapterInfo.page
        val chapter get() = pageWithChapterInfo.chapter
    }

    data class Ads(val chapterId: Long) : ReaderPage(chapterId + 1000000)
}