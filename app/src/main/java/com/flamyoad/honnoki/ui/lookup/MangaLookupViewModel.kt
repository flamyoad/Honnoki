package com.flamyoad.honnoki.ui.lookup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flamyoad.honnoki.data.entities.LookupResult
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import kotlinx.coroutines.flow.Flow

class MangaLookupViewModel(
    private val params: String,
    private val mangaSource: BaseSource,
    private val lookupType: LookupType
) : ViewModel() {

    val lookupResult: Flow<PagingData<LookupResult>> = when (lookupType) {
        LookupType.GENRE -> mangaSource.getMangaByGenres(params)
        LookupType.AUTHOR -> mangaSource.getMangaByAuthors(params)
    }.cachedIn(viewModelScope)
}