package com.flamyoad.honnoki.ui.home

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.source.MangakalotSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

@ExperimentalPagingApi
class HomeViewModel(val db: AppDatabase, val mangaSource: BaseSource) : ViewModel() {

    private val shouldShrinkFab = MutableLiveData<Boolean>(false)
    fun shouldShrinkFab(): LiveData<Boolean> = shouldShrinkFab

    fun getRecentManga(): Flow<PagingData<Manga>> = mangaSource.getRecentManga()
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

    fun getTrendingManga(): Flow<PagingData<Manga>> = mangaSource.getTrendingManga()
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

    fun getNewManga(): Flow<PagingData<Manga>> = mangaSource.getNewManga()
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

    fun setShouldShrinkFab(boolean: Boolean) {
        shouldShrinkFab.value = boolean
    }

    fun getSourceType(): Source = mangaSource.getSourceType()
}