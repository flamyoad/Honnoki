package com.flamyoad.honnoki.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.databinding.SimpleSearchResultItemBinding
import com.flamyoad.honnoki.model.SearchResult

class SimpleSearchResultAdapter(private val onItemClick: (SearchResult) -> Unit) :
    PagingDataAdapter<SearchResult, SimpleSearchResultAdapter.ResultViewHolder>(
        SIMPLE_SEARCH_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val layout = SimpleSearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ResultViewHolder(val binding: SimpleSearchResultItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResult: SearchResult) {
            val loadingIndicator = CircularProgressDrawable(itemView.context).apply {
                setColorSchemeColors(Color.BLACK)
                centerRadius = 10f
                strokeWidth = 4f
            }
            loadingIndicator.start()

            with(binding) {
                txtAuthor.paintFlags = txtAuthor.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                Glide.with(itemView.context)
                    .load(searchResult.coverImage)
                    .placeholder(loadingIndicator)
                    .into(coverImage)

                txtTitle.text = searchResult.title
                txtAuthor.text = searchResult.author
                txtLatestChapter.text = searchResult.latestChapter
            }
        }
    }

    companion object {
        val SIMPLE_SEARCH_COMPARATOR = object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem == newItem
            }
        }
    }
}