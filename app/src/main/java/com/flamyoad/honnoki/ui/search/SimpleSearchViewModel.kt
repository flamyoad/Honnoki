package com.flamyoad.honnoki.ui.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.ui.search.model.SearchGenre
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.source.model.Source
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

    private val selectedGenre = MutableStateFlow(GenreConstants.ALL)
    fun selectedGenre() = selectedGenre.asStateFlow()

    private val selectedSource = MutableStateFlow(Source.MANGAKALOT)
    fun selectedSource() = selectedSource.asStateFlow()

    private val genreList = MutableStateFlow(initializeGenreList())
    fun genreList() = genreList
        .filter { it.isNotEmpty() }
        .combineTransform(selectedGenre) { list, selectedGenre ->
            val newList = list.map {
                it.copy(isSelected = it.genre == selectedGenre)
            }
            emit(newList)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val sourceList = MutableStateFlow(initializeSourceList())
    fun sourceList() = sourceList
        .filter { it.isNotEmpty() }
        .combineTransform(selectedSource) { list, selectedSource ->
            val newList = list.map {
                it.copy(isSelected = it.source == selectedSource)
            }
            emit(newList)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val searchQuery = MutableStateFlow("")

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
            } else {
                return@flatMapLatest sourceImpl.getSimpleSearchWithGenre(query, genre)
            }
        }
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)

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

    private fun initializeSourceList(): List<SearchSource> {
        return Source.values()
            .filter { it.isEnabled }
            .map { SearchSource(source = it, isSelected = it == Source.MANGAKALOT) }
    }

    fun selectGenre(genre: GenreConstants) {
        selectedGenre.value = genre
    }

    fun selectSource(source: Source) {
        selectedSource.value = source
    }
}