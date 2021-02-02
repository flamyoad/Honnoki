package com.flamyoad.honnoki.repository

import androidx.paging.PagingData
import com.flamyoad.honnoki.model.Manga
import kotlinx.coroutines.flow.Flow

interface BaseMangaRepository {
    fun getRecentManga(): Flow<PagingData<Manga>>
    fun getTrendingManga(): Flow<PagingData<Manga>>
}