package com.flamyoad.honnoki.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.SearchResult
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.source.MangakalotSource
import com.flamyoad.honnoki.ui.search.model.SearchGenre
import com.flamyoad.honnoki.data.GenreConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class SimpleSearchViewModel(
    private val app: Application,
    private val db: AppDatabase,
    private val baseSource: BaseSource
) : ViewModel() {

    private val applicationContext get() = app.applicationContext

    private val genreList = MutableStateFlow(initializeGenreList())
    fun genreList() = genreList.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    private val searchGenre = MutableStateFlow(GenreConstants.ALL)

    val searchResult: Flow<PagingData<SearchResult>> = searchQuery
        .debounce(500)
        .combine(searchGenre) { query, genreConstant -> return@combine Pair(query, genreConstant) }
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
                enumOrdinal = it.ordinal,
                isSelected = it == GenreConstants.ALL
            )
        }
    }

    fun selectGenre(genre: SearchGenre) {
        searchGenre.value = GenreConstants.getByOrdinal(genre.enumOrdinal) ?: return

        val prevList = genreList.value
        val newList = prevList.map {
            it.copy(isSelected = it == genre)
        }
        genreList.value = newList
    }
}