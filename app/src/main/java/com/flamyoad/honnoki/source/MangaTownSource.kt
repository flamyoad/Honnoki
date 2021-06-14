package com.flamyoad.honnoki.source

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flamyoad.honnoki.api.MangaTownApi
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.MangaType
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.paging.MangaMediator
import kotlinx.coroutines.flow.Flow

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

    companion object {
        private const val LOW_PAGINATION_SIZE = 20
        private const val NORMAL_PAGINATION_SIZE = 30
    }

}