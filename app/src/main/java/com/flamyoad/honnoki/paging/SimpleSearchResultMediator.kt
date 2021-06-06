package com.flamyoad.honnoki.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.flamyoad.honnoki.api.BaseApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.SearchResult
import com.flamyoad.honnoki.model.Source
import com.flamyoad.honnoki.utils.GenreConstants
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class SimpleSearchResultMediator(
    private val api: BaseApi,
    private val db: AppDatabase,
    private val keyword: String,
    private val genre: GenreConstants,
    private val source: Source
) : RemoteMediator<Int, SearchResult>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchResult>
    ): MediatorResult {
        val lastItem = state.lastItemOrNull()

        val pageNumber = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
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
            val searchedResults = if (genre == GenreConstants.ALL) {
                api.searchByKeyword(keyword, pageNumber)
            } else {
                api.searchByKeywordAndGenres(keyword, genre, pageNumber)
            }

            val endOfPaginationReached = searchedResults.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.searchResultDao().deleteAll()
                }

                val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
                val nextKey = if (endOfPaginationReached) null else pageNumber + 1

                val searchedResultsWithKeys =
                    searchedResults.map { it.copy(prevKey = prevKey, nextKey = nextKey) }
                db.searchResultDao().insertAll(searchedResultsWithKeys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}