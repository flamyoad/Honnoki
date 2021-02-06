package com.flamyoad.honnoki.ui.overview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flamyoad.honnoki.model.Genre

class MangaOverviewViewModel(application: Application) : AndroidViewModel(application) {

    private val genreList = MutableLiveData<List<Genre>>()
    fun genreList(): LiveData<List<Genre>> = genreList

    init {
        val genres = listOf(
            Genre(null, "Shounen", "", ""),
            Genre(null, "少女", "", ""),
            Genre(null, "寝取られ", "", ""),
            Genre(null, "此地3银", "", ""),
            Genre(null, "1212121221", "", "")
        )
        genreList.value = genres
    }
}