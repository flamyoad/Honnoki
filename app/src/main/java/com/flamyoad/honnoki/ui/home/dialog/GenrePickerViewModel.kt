package com.flamyoad.honnoki.ui.home.dialog

import androidx.lifecycle.ViewModel
import com.flamyoad.honnoki.data.GenreConstants

class GenrePickerViewModel: ViewModel() {
    val genreList = GenreConstants.values().toList()
}