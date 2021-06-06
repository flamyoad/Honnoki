package com.flamyoad.honnoki.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.SearchResult
import com.flamyoad.honnoki.repository.BaseMangaRepository
import com.flamyoad.honnoki.repository.MangakalotRepository
import com.flamyoad.honnoki.ui.search.model.SearchGenre
import com.flamyoad.honnoki.utils.GenreConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class SimpleSearchViewModel(val app: Application) : AndroidViewModel(app) {
    private val applicationContext get() = app.applicationContext

    private val db = AppDatabase.getInstance(app)
    private var mangaRepo: BaseMangaRepository = MangakalotRepository(db, app.applicationContext)

    private val genreList = MutableStateFlow(initializeGenreList())
    fun genreList() = genreList.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    val searchResult: Flow<PagingData<SearchResult>> = searchQuery
        .debounce(500)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                return@flatMapLatest emptyFlow()
            }
            return@flatMapLatest mangaRepo.getSimpleSearch(query).cachedIn(viewModelScope)
        }

    fun submitQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.searchResultDao().deleteAll()
            searchQuery.emit(query)
        }
    }

    private fun initializeGenreList(): List<SearchGenre> {
        val list = GenreConstants.values().map {
            SearchGenre(name = it.toReadableName(app.applicationContext), isSelected = false)
        }.toMutableList()

        // This is the "All" item in first row
        list.add(0, SearchGenre.getDefaultItem(applicationContext))
        return list
    }

    fun selectGenre(genre: SearchGenre) {
        val prevList = genreList.value
        val newList = prevList.map {
            it.copy(isSelected = it == genre)
        }
        genreList.value = newList
    }
}