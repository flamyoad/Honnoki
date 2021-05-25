package com.flamyoad.honnoki.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.SearchResult
import com.flamyoad.honnoki.repository.BaseMangaRepository
import com.flamyoad.honnoki.repository.MangakalotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class SimpleSearchViewModel(val app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private var mangaRepo: BaseMangaRepository = MangakalotRepository(db, app.applicationContext)

    private val searchQuery = MutableStateFlow("")

    val searchResult: Flow<PagingData<SearchResult>> = searchQuery
        .debounce(500)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                return@flatMapLatest emptyFlow()
            }
//            val encodedQuery =
            return@flatMapLatest mangaRepo.getSimpleSearch(query).cachedIn(viewModelScope)
        }

    fun submitQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.searchResultDao().deleteAll()
            searchQuery.emit(query)
        }
    }
}