package com.flamyoad.honnoki.repository

import androidx.paging.PagingData
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.Source
import kotlinx.coroutines.flow.Flow

abstract class BaseMangaRepository(val db: AppDatabase) {
    abstract fun getRecentManga(): Flow<PagingData<Manga>>
    abstract fun getTrendingManga(): Flow<PagingData<Manga>>
    abstract fun getSourceType(): Source
}