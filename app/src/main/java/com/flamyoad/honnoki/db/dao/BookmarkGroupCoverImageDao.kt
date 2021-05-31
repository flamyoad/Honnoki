package com.flamyoad.honnoki.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkGroupCoverImageDao {
    @Transaction
    @Query(
        """
        SELECT bg.*, COUNT(bookmark.id) AS item_count, (manga_overview.coverImage) AS cover_image
        FROM bookmark_group as bg
        LEFT OUTER JOIN bookmark ON bg.id = bookmark.bookmarkGroupId
        LEFT OUTER JOIN manga_overview ON bookmark.mangaOverviewId = manga_overview.id
        GROUP BY bg.id
    """
    )
    fun getAll(): Flow<List<BookmarkGroupWithCoverImages>>
}