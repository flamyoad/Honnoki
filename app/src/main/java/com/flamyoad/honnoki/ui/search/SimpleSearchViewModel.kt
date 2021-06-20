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
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named

@ExperimentalPagingApi
class SimpleSearchViewModel(
    private val app: Application,
    private val db: AppDatabase,
) : ViewModel(), KoinComponent {

    private val genreList = MutableStateFlow(initializeGenreList())
    fun genreList() = genreList.asStateFlow()

    private val sourceList = MutableStateFlow(initializeSourceList())
    fun sourceList() = sourceList.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    private val selectedGenre = MutableStateFlow(GenreConstants.ALL)

    private val selectedSource = MutableStateFlow(Source.MANGAKALOT)
    fun selectedSource() = selectedSource.asStateFlow()

    val searchResult: Flow<PagingData<SearchResult>> = searchQuery
        .debounce(500)
        .combine(selectedGenre) { query, genre -> Pair(query, genre) }
        .combine(selectedSource) { (query, genre), source -> Triple(query, genre, source) }
        .flatMapLatest { (query, genre, source) ->
            // Get the BaseSource from Koin container
            val sourceImpl: BaseSource = getKoin().get(named(source.name))

            if (query.isBlank() && genre == GenreConstants.ALL) {
                return@flatMapLatest flowOf(PagingData.empty())
            }

            if (genre == GenreConstants.ALL) {
                return@flatMapLatest sourceImpl.getSimpleSearch(query)
                    .cachedIn(viewModelScope)

            } else {
                return@flatMapLatest sourceImpl.getSimpleSearchWithGenre(query, genre)
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