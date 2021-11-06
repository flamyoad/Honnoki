package com.flamyoad.honnoki.ui.home.dialog

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.DynamicGenre
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.source.model.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenrePickerViewModel(
    private val source: BaseSource,
    private val application: Application
) : ViewModel() {
    private val dynamicGenres =
        MutableStateFlow<State<List<DynamicGenre>>>(State.Loading)

    fun dynamicGenres() = dynamicGenres.asStateFlow()

    init {
        initializeGenres()
    }

    private fun initializeGenres() {
        if (source.getSourceType() == Source.MANGADEX) {
            viewModelScope.launch {
                dynamicGenres.value = source.getDynamicGenres()
            }
        } else {
            val genreList = GenreConstants.values()
                .filter { it != GenreConstants.ALL }
                .map {
                    DynamicGenre(
                        id = "",
                        name = it.toReadableName(application.applicationContext),
                        constantValue = it,
                    )
                }
            dynamicGenres.value = State.Success(genreList)
        }
    }
}