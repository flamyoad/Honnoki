package com.flamyoad.honnoki.ui.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.SearchResult
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.ui.search.model.SearchGenre
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.ui.search.model.SearchSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class SimpleSearchViewModel(
    private val app: Application,
    private val db: AppDatabase,
    private val baseSource: BaseSource,
) : ViewModel() {

    private val genreList = MutableStateFlow(initializeGenreList())
    fun genreList() = genreList.asStateFlow()

    private val sourceList = MutableStateFlow(initializeSourceList())
    fun sourceList() = sourceList.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    private val selectedGenre = MutableStateFlow(GenreConstants.ALL)

    private val selectedSource = MutableStateFlow(Source.MANGAKALOT)

    val searchResult: Flow<PagingData<SearchResult>> = searchQuery
        .debounce(500)
        .combine(selectedGenre) { query, genreConstant ->
            return@combine Pair(
                query,
                genreConstant
            )
        }
        .flatMapLatest { (query, genre) ->
            if (query.isBlank() && genre == GenreConstants.ALL) {
                return@flatMapLatest flowOf(PagingData.empty())
            }

            if (genre == GenreConstants.ALL) {
                return@flatMapLatest baseSource.getSimpleSearch(query)
                    .cachedIn(viewModelScope)

            } else {
                return@flatMapLatest baseSource.getSimpleSearchWithGenre(query, genre)
                    .cachedIn(viewModelScope)
            }
        }
        .flowOn(Dispatchers.IO)

    fun submitQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchQuery.emit(query)
        }
    }

    private fun initializeGenreList(): List<SearchGenre> {
        return GenreConstants.values().map {
            SearchGenre(
                name = it.toReadableName(app.applicationContext),
                genre = it,
                isSelected = it == GenreConstants.ALL
            )
        }
    }

    fun selectGenre(searchGenre: SearchGenre) {
        selectedGenre.value = searchGenre.genre

        val prevList = genreList.value
        val newList = prevList.map {
            it.copy(isSelected = it == searchGenre)
        }
        genreList.value = newList
    }

    private fun initializeSourceList(): List<SearchSource> {
        return Source.values()
            .filter { it.isEnabled }
            .map { SearchSource(source = it, isSelected = it == Source.MANGAKALOT) }
    }

    fun selectSource(searchSource: SearchSource) {
        selectedSource.value = searchSource.source

        val prevList = sourceList.value
        val newList = prevList.map {
            it.copy(isSelected = it == searchSource)
        }
        sourceList.value = newList
    }
}