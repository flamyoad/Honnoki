package com.flamyoad.honnoki.source

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.MangaTownApi
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.paging.MangaMediator
import kotlinx.coroutines.flow.Flow
import java.io.IOException

@ExperimentalPagingApi
class MangaTownSource(db: AppDatabase, context: Context, private val api: MangaTownApi): BaseSource(db, context) {

    override fun getSourceType(): Source {
        return Source.MANGATOWN
    }

    override fun getRecentManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = NORMAL_PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, getSourceType(), MangaType.RECENTLY),
            pagingSourceFactory = { db.mangaDao().getFrom(getSourceType(), MangaType.RECENTLY) }
        ).flow
    }

    override fun getTrendingManga(): Flow<PagingData<Manga>> {
        return Pager(
            config = PagingConfig(pageSize = NORMAL_PAGINATION_SIZE, enablePlaceholders = true),
            remoteMediator = MangaMediator(api, db, getSourceType(), MangaType.TRENDING),
            pagingSourceFactory = { db.mangaDao().getFrom(getSourceType(), MangaType.TRENDING) }
        ).flow
    }

    override suspend fun getMangaOverview(urlPath: String): State<MangaOverview> {
        return api.searchForMangaOverview(urlPath)
    }

    override suspend fun getAuthors(urlPath: String): State<List<Author>> {
        return api.searchForAuthors(urlPath)
    }

    override suspend fun getChapterList(urlPath: String): State<List<Chapter>> {
        return api.searchForChapterList(urlPath)
    }

    override suspend fun getGenres(urlPath: String): State<List<Genre>> {
        return api.searchForGenres(urlPath)
    }

    override suspend fun getImages(urlPath: String): State<List<Page>> {
        try {
            val pages = api.searchForPageList(urlPath)
            val pagesWithImages = mutableListOf<Page>()

            var count = 1
            for (page in pages) {
                val result = api.getImageFromPage(page, count)
                if (result is State.Success) {
                    pagesWithImages.add(result.value)
                    count++
                }
            }
            return State.Success(pagesWithImages)

        } catch (e: IOException) {
            return State.Error(e)
        }
    }

    companion object {
        private const val LOW_PAGINATION_SIZE = 20
        private const val NORMAL_PAGINATION_SIZE = 30
    }

}