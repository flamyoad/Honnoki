package com.flamyoad.honnoki.ui.reader

import com.flamyoad.honnoki.model.Page

sealed class ReaderPage {
    data class Value (val page: Page): ReaderPage()
    data class Ads(val chapterId: Long): ReaderPage()
    object LoadingIndicator: ReaderPage()
}