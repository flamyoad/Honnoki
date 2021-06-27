package com.flamyoad.honnoki.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.flamyoad.honnoki.api.BaseApi
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.source.model.Source
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class MangaMediator(
    private val api: BaseApi,
    private val db: AppDatabase,
    private val source: Source,
    private val mangaType: MangaType
) : RemoteMediator<Int, Manga>() {

    private val STARTING_PAGE_INDEX get() = api.startingPageIndex

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Manga>): MediatorResult {
        val lastItem = state.lastItemOrNull()

        val pageNumber = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                if (lastItem == null) {
                    return MediatorResult.Success(endOfPaginationReached = false)
                }
                if (lastItem.nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                lastItem.nextKey
            }
        }

        try {
            val mangas = when (mangaType) {
                MangaType.RECENTLY -> api.searchForLatestManga(pageNumber)
                MangaType.TRENDING -> api.searchForTrendingManga(pageNumber)
                MangaType.TOP -> api.searchForTopManga(1) // Only have first page
                MangaType.NEW -> api.searchForNewManga(pageNumber)
            }
            val endOfPaginationReached = mangas.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.mangaDao().deleteFrom(source, mangaType)
                }

                val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
                val nextKey = if (endOfPaginationReached) null else pageNumber + 1

                val mangasWithKeys = mangas.map { it.copy(prevKey = prevKey, nextKey = nextKey) }
                db.mangaDao().insertAll(mangasWithKeys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }
}