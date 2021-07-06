package com.flamyoad.honnoki.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.databinding.HorizontalMangaListBinding
import com.flamyoad.honnoki.utils.ui.onItemsArrived
import com.flamyoad.honnoki.utils.ui.willConsumeHorizontalScrolls

class HorizontalMangaAdapter(
    private val context: Context,
    private val onItemClick: (Manga) -> Unit,
    private val onItemsLoaded: () -> Unit
) :
    RecyclerView.Adapter<HorizontalMangaAdapter.ListViewHolder>() {

    private val listAdapter = HorizontalMangaListAdapter(onItemClick)
    private val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = HorizontalMangaListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder.binding.listManga) {
            adapter = listAdapter
            layoutManager = linearLayoutManager
            stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
            willConsumeHorizontalScrolls()
        }

        listAdapter.onItemsArrived {
            onItemsLoaded.invoke()
        }
    }

    inner class ListViewHolder(val binding: HorizontalMangaListBinding) :
        RecyclerView.ViewHolder(binding.root)

    suspend fun submitDataToChild(pagingData: PagingData<Manga>) {
        listAdapter.submitData(pagingData)
    }

    fun refresh() {
        listAdapter.refresh()
    }
}