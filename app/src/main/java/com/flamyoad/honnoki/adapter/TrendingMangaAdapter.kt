package com.flamyoad.honnoki.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.MangaTrendingListBinding
import com.flamyoad.honnoki.model.Manga

class TrendingMangaAdapter(context: Context, onItemClick: (Manga) -> Unit) :
    RecyclerView.Adapter<TrendingMangaAdapter.ListViewHolder>() {

    private val listAdapter = TrendingMangaListAdapter(onItemClick)
    private val linearLayoutManager =
        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            MangaTrendingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        with(holder.binding.listManga) {
            adapter = listAdapter
            layoutManager = linearLayoutManager
            stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.action) {
                        MotionEvent.ACTION_DOWN -> {
                            parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }
    }

    inner class ListViewHolder(val binding: MangaTrendingListBinding) :
        RecyclerView.ViewHolder(binding.root)

    suspend fun submitDataToChild(pagingData: PagingData<Manga>) {
        listAdapter.submitData(pagingData)
    }

    fun refresh() {
        listAdapter.refresh()
    }
}