package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.MangaRecentListHeaderBinding

class RecentMangaHeaderAdapter(private val onMoreButtonClick: () -> Unit) :
    RecyclerView.Adapter<RecentMangaHeaderAdapter.HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val layout = MangaRecentListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = HeaderViewHolder(layout)

        layout.button.setOnClickListener {
            onMoreButtonClick.invoke()
        }

        return holder
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {}

    override fun getItemCount(): Int = 1

    inner class HeaderViewHolder(binding: MangaRecentListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}