package com.flamyoad.honnoki

import com.flamyoad.honnoki.adapter.BookmarkGroupAdapter
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithInfo
import org.junit.Assert
import org.junit.Test

class DataClassEqualityTest {

    private val comparator = BookmarkGroupAdapter.GROUP_COMPARATOR

    @Test
    fun `areItemsTheSame() returns true when both items have same id`() {
        val first = BookmarkGroupWithInfo(
            bookmarkGroup = BookmarkGroup(id = 1, name = "All", isSelected = false),
            coverImageList = emptyList()
        )

        val second = BookmarkGroupWithInfo(
            bookmarkGroup = BookmarkGroup(id = 1, name = "Recents", isSelected = false),
            coverImageList = emptyList()
        )

        val areItemsTheSame = comparator.areItemsTheSame(first, second)

        Assert.assertEquals(areItemsTheSame, true)
    }

    @Test
    fun `areItemsTheSame() returns false when both items have different id`() {
        val first = BookmarkGroupWithInfo(
            bookmarkGroup = BookmarkGroup(id = 1, name = "All", isSelected = false),
            coverImageList = emptyList()
        )

        val second = BookmarkGroupWithInfo(
            bookmarkGroup = BookmarkGroup(id = 2, name = "Recents", isSelected = false),
            coverImageList = emptyList()
        )

        val areItemsTheSame = comparator.areItemsTheSame(first, second)

        Assert.assertEquals(areItemsTheSame, false)
    }
}