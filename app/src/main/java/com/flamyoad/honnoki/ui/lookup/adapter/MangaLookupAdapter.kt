package com.flamyoad.honnoki.ui.lookup.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.databinding.VerticalMangaListItemBinding

class MangaLookupAdapter(private val onItemClick: (SearchResult) -> Unit) :
    PagingDataAdapter<SearchResult, MangaLookupAdapter.ResultViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val layout =
            VerticalMangaListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ResultViewHolder(layout)

        holder.itemView.setOnClickListener {
            val searchResult = getItem(holder.bindingAdapterPosition) ?: return@setOnClickListener
            onItemClick.invoke(searchResult)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class ResultViewHolder(val binding: VerticalMangaListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResult: SearchResult) {
            val loadingIndicator = CircularProgressDrawable(itemView.context).apply {
                setColorSchemeColors(Color.BLACK)
                centerRadius = 25f
                strokeWidth = 10f
            }
            loadingIndicator.start()

            with(binding) {
                Glide.with(itemView.context)
                    .load(searchResult.coverImage)
                    .placeholder(loadingIndicator)
                    .into(coverImage)

                txtTitle.text = searchResult.title
                txtInformation.text = searchResult.latestChapter
            }
        }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem == newItem
            }
        }
    }
}