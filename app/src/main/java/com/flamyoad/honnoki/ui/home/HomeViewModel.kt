package com.flamyoad.honnoki.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.repository.MangakalotRepository
import com.flamyoad.honnoki.repository.SenMangaRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
//    private val mangaRepo = MangakalotRepository(db)
    private val mangaRepo = SenMangaRepository(db)

    private val shouldShrinkFab = MutableLiveData<Boolean>(false)
    fun shouldShrinkFab(): LiveData<Boolean> = shouldShrinkFab

    fun getRecentManga(): Flow<PagingData<Manga>> {
        return mangaRepo.getRecentManga().cachedIn(viewModelScope)
    }

    fun getTrendingManga(): Flow<PagingData<Manga>> {
        return mangaRepo.getTrendingManga().cachedIn(viewModelScope)
    }

    fun setShouldShrinkFab(boolean: Boolean) {
        shouldShrinkFab.value = boolean
    }
}