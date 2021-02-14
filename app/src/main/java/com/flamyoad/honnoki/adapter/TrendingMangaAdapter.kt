package com.flamyoad.honnoki.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.MangaTrendingListBinding
import com.flamyoad.honnoki.model.Manga

class TrendingMangaAdapter(private val context: Context, onItemClick: (Manga) -> Unit) :
    RecyclerView.Adapter<TrendingMangaAdapter.ListViewHolder>() {

    private val listAdapter = TrendingMangaListAdapter(onItemClick)
    private val linearLayoutManager =
        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manga_trending_list, parent, false)

        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder.binding) {
            listManga.adapter = listAdapter
            listManga.layoutManager = linearLayoutManager
        }
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = MangaTrendingListBinding.bind(itemView)
    }

    suspend fun submitDataToChild(pagingData: PagingData<Manga>) {
        listAdapter.submitData(pagingData)
    }

    fun refresh() {
        listAdapter.refresh()
    }
}