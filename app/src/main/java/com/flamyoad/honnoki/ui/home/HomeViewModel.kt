package com.flamyoad.honnoki.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.repository.MangakalotRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val mangaRepo = MangakalotRepository(db)

    private val shouldShrinkFab = MutableLiveData<Boolean>(false)
    fun shouldShrinkFab(): LiveData<Boolean> = shouldShrinkFab

    fun getRecentManga(): Flow<PagingData<Manga>> {
        return mangaRepo.getRecentManga()
    }

    fun setShouldShrinkFab(boolean: Boolean) {
        shouldShrinkFab.value = boolean
    }
}