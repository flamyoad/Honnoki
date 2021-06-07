package com.flamyoad.honnoki.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.Source
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.source.MangakalotSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

@ExperimentalPagingApi
class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private var mangaSource: BaseSource = MangakalotSource(db, app.applicationContext)

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

    fun switchMangaSource(source: Source) {
        mangaSource = BaseSource.get(source, db, app.applicationContext)
    }

    fun getSourceType(): Source = mangaSource.getSourceType()

}