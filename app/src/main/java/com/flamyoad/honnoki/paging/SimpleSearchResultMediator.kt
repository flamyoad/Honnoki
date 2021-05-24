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
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class SimpleSearchResultMediator(
    private val api: BaseApi,
    private val db: AppDatabase,
    private val keyword: String,
    private val source: Source
) : RemoteMediator<Int, SearchResult>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, SearchResult>): MediatorResult {
        val lastItem = state.lastItemOrNull()

        val pageNumber = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> lastItem?.nextKey ?: return MediatorResult.Success(
                endOfPaginationReached = true
            )
        }

        try {
            val searchedResults = api.searchByKeyword(keyword, pageNumber)

            val endOfPaginationReached = searchedResults.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.searchResultDao().deleteAll()
                }

                val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
                val nextKey = if (endOfPaginationReached) null else pageNumber + 1

                val searchedResultsWithKeys = searchedResults.map { it.copy(prevKey = prevKey, nextKey = nextKey) }
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