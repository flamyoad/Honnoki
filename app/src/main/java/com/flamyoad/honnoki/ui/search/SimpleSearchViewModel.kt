package com.flamyoad.honnoki.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Genre
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

    private val searchGenre = MutableStateFlow(GenreConstants.ALL)

    val searchResult: Flow<PagingData<SearchResult>> = searchQuery
        .debounce(500)
        .combine(searchGenre) { query, genreConstant -> return@combine Pair(query, genreConstant) }
        .flatMapLatest { (query, genre) ->
            if (query.isBlank() && genre == GenreConstants.ALL) {
                return@flatMapLatest flowOf(PagingData.empty())
            }

            if (genre == GenreConstants.ALL) {
                return@flatMapLatest mangaRepo.getSimpleSearch(query)
                    .cachedIn(viewModelScope)

            } else {
                return@flatMapLatest mangaRepo.getSimpleSearchWithGenre(query, genre)
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
                isSelected = it == GenreConstants.ALL)
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