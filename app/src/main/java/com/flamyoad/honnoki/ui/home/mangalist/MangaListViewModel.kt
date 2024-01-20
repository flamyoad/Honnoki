package com.flamyoad.honnoki.ui.home.mangalist

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.source.BaseSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class MangaListViewModel(val db: AppDatabase, val mangaSource: BaseSource) : ViewModel() {

    fun getRecentManga(): Flow<PagingData<Manga>> = mangaSource.getRecentManga()
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

    fun getTrendingManga(): Flow<PagingData<Manga>> = mangaSource.getTrendingManga()
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

    fun getNewManga(): Flow<PagingData<Manga>> = mangaSource.getNewManga()
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
}