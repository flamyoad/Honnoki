package com.flamyoad.honnoki.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.flamyoad.honnoki.BuildConfig
import com.flamyoad.honnoki.api.BaseApi
import com.flamyoad.honnoki.api.handler.ApiException
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.LookupResult
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.flamyoad.honnoki.api.handler.ApiException.Code
import com.google.firebase.crashlytics.FirebaseCrashlytics

@ExperimentalPagingApi
class LookupResultMediator(
    private val api: BaseApi,
    private val db: AppDatabase,
    private val params: String,
    private val lookupType: LookupType,
) : RemoteMediator<Int, LookupResult>() {

    private val STARTING_PAGE_INDEX get() = api.startingPageIndex

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LookupResult>
    ): MediatorResult {
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

        val apiResult = when (lookupType) {
            LookupType.GENRE -> api.searchMangaByGenre(params, pageNumber)
            LookupType.AUTHOR -> api.searchMangaByAuthor(params, pageNumber)
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
        val lookupResult = data.map {
            LookupResult(
                link = it.link,
                coverImage = it.coverImage,
                title = it.title,
                author = it.author,
                latestChapter = it.latestChapter
            )
        }

        val endOfPaginationReached = lookupResult.isEmpty()

        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                db.lookupDao().deleteAll()
            }

            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (endOfPaginationReached) null else pageNumber + 1

            val lookupResultWithKey =
                lookupResult.map { it.copy(prevKey = prevKey, nextKey = nextKey) }
            db.lookupDao().insertAll(lookupResultWithKey)
        }
        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }

    private fun handleError(throwable: Throwable?): MediatorResult.Error {
        throwable?.let {
            if (it is ApiException) {
                if (it.code != Code.NoInternetConnection) {
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
            }
        }
        return MediatorResult.Error(throwable ?: Exception())
    }
}