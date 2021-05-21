package com.flamyoad.honnoki.ui.overview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.db.AppDatabase
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.MangaOverview
import com.flamyoad.honnoki.model.Source
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.repository.BaseMangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import kotlin.random.Random

@ExperimentalPagingApi
class MangaOverviewViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)

    private lateinit var mangaRepo: BaseMangaRepository

    private var mangaOverview = MutableLiveData<State<MangaOverview>>(State.Loading)
    fun mangaOverview(): LiveData<State<MangaOverview>> = mangaOverview

    private var chapterList = MutableLiveData<State<List<Chapter>>>(State.Loading)
    fun chapterList(): LiveData<State<List<Chapter>>> = chapterList

    fun initMangaOverview(url: String, sourceName: String) {
        val source = try {
            Source.valueOf(sourceName)
        } catch (e: IllegalArgumentException) {
            return
        }

        mangaRepo = BaseMangaRepository.get(source, db, app.applicationContext)
        loadMangaOverview(url)
    }

    fun loadMangaOverview(url: String) {
        viewModelScope.launch {
            val overview = mangaRepo.getMangaOverview(url)
            mangaOverview.postValue(overview)

            val chapters = mangaRepo.getChapterList(url)
            chapterList.postValue(chapters)
        }
    }

    fun sortChapterList(isAscending: Boolean) {
        if (chapterList.value !is State.Success) {
            return
        }

        val oldChapterList = chapterList.value
        val oldChapterListItems = (oldChapterList as State.Success).value

        val x = Random.nextBoolean()

        viewModelScope.launch(Dispatchers.Default) {
            val newChapterListItems = if (x) {
                oldChapterListItems.sortedBy { chapter -> chapter.title }
            } else {
                oldChapterListItems.sortedByDescending { chapter -> chapter.title }
            }
            chapterList.postValue(State.Success(newChapterListItems))
        }
    }
}