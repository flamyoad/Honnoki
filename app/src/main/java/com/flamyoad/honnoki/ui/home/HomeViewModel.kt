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
import com.flamyoad.honnoki.repository.BaseMangaRepository
import com.flamyoad.honnoki.repository.MangakalotRepository
import com.flamyoad.honnoki.repository.SenMangaRepository
import kotlinx.coroutines.flow.Flow

@ExperimentalPagingApi
class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private var mangaRepo: BaseMangaRepository = MangakalotRepository(db, app.applicationContext)

    private val shouldShrinkFab = MutableLiveData<Boolean>(false)
    fun shouldShrinkFab(): LiveData<Boolean> = shouldShrinkFab

    fun getRecentManga(): Flow<PagingData<Manga>> = mangaRepo.getRecentManga().cachedIn(viewModelScope)
    fun getTrendingManga(): Flow<PagingData<Manga>> = mangaRepo.getTrendingManga().cachedIn(viewModelScope)

    fun setShouldShrinkFab(boolean: Boolean) {
        shouldShrinkFab.value = boolean
    }

    fun switchMangaSource(source: Source) {
        mangaRepo = BaseMangaRepository.get(source, db, app.applicationContext)
    }

    fun getSourceType(): Source = mangaRepo.getSourceType()

}