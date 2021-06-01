package com.flamyoad.honnoki.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Page
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.repository.BaseMangaRepository
import com.flamyoad.honnoki.repository.MangakalotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class ReaderFrameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private var mangaRepo: BaseMangaRepository = MangakalotRepository(db, application.applicationContext)

    private val pageList = MutableLiveData<State<List<Page>>>()
    fun pageList(): LiveData<State<List<Page>>> = pageList

    fun fetchManga(link: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = mangaRepo.getImages(link)
            pageList.postValue(result)
        }
    }
}