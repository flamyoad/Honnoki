package com.flamyoad.honnoki.ui.reader.model

import com.flamyoad.honnoki.model.PageWithChapterInfo

sealed class ReaderPage {
    data class Value (val pageWithChapterInfo: PageWithChapterInfo): ReaderPage() {
        val page get() = pageWithChapterInfo.page
        val chapter get() = pageWithChapterInfo.chapter
    }

    data class Ads(val chapterId: Long): ReaderPage()
}