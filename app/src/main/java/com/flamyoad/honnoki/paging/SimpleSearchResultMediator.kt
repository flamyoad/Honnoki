package com.flamyoad.honnoki.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.flamyoad.honnoki.api.BaseApi
import com.flamyoad.honnoki.api.handler.ApiException
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.data.entities.Manga
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import java.io.IOException

class SimpleSearchResultMediator(
    private val api: BaseApi,
    private val db: AppDatabase,
    private val keyword: String,
    private val genre: GenreConstants,
) : RemoteMediator<Int, SearchResult>() {

    private val STARTING_PAGE_INDEX get() = api.startingPageIndex

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

        val apiResult = when (genre) {
            GenreConstants.ALL -> api.searchByKeyword(keyword, pageNumber)
            else -> api.searchByKeywordAndGenres(keyword, genre, pageNumber)
        }

        return when (apiResult) {
            is State.Success -> handleSuccess(apiResult.value, loadType, pageNumber)
            is State.Error -> handleError(apiResult.exception)
            is State.Loading -> MediatorResult.Error(Exception())
        }
    }

    private suspend fun handleSuccess(
        data: List<SearchResult>,
        loadType: LoadType,
        pageNumber: Int
    ): MediatorResult.Success {
        val endOfPaginationReached = data.isEmpty()

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                db.searchResultDao().deleteAll()
            }

            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (endOfPaginationReached) null else pageNumber + 1

            val searchedResultsWithKeys =
                data.map { it.copy(prevKey = prevKey, nextKey = nextKey) }
            db.searchResultDao().insertAll(searchedResultsWithKeys)
        }
        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }

    private fun handleError(throwable: Throwable?): MediatorResult.Error {
        throwable?.let {
            if (it is ApiException) {
                if (it.code != ApiException.Code.NoInternetConnection) {
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
            }
        }
        return MediatorResult.Error(throwable ?: Exception())
    }
}