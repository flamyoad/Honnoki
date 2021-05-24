package com.flamyoad.honnoki.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.SearchResult
import com.flamyoad.honnoki.repository.BaseMangaRepository
import com.flamyoad.honnoki.repository.MangakalotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalPagingApi
class SimpleSearchViewModel(val app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private var mangaRepo: BaseMangaRepository = MangakalotRepository(db, app.applicationContext)

    private var searchResult: Flow<PagingData<SearchResult>> = emptyFlow()
    fun searchResult(): Flow<PagingData<SearchResult>> = searchResult

    fun submitQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.searchResultDao().deleteAll()

            withContext(Dispatchers.Main) {
                searchResult = mangaRepo.getSimpleSearch(query).cachedIn(viewModelScope)
            }
        }
    }
}