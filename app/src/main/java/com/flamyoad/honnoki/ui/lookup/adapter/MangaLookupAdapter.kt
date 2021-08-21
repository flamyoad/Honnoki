package com.flamyoad.honnoki.ui.lookup.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.data.entities.LookupResult
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.databinding.VerticalMangaListItemBinding

class MangaLookupAdapter(private val onItemClick: (LookupResult) -> Unit) :
    PagingDataAdapter<LookupResult, MangaLookupAdapter.ResultViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val layout =
            VerticalMangaListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ResultViewHolder(layout)

        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            val lookupResult = getItem(position) ?: return@setOnClickListener
            onItemClick.invoke(lookupResult)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class ResultViewHolder(val binding: VerticalMangaListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lookupResult: LookupResult) {
            val loadingIndicator = CircularProgressDrawable(itemView.context).apply {
                setColorSchemeColors(Color.BLACK)
                centerRadius = 25f
                strokeWidth = 10f
            }
            loadingIndicator.start()

            with(binding) {
                Glide.with(itemView.context)
                    .load(lookupResult.coverImage)
                    .placeholder(loadingIndicator)
                    .into(coverImage)

                txtTitle.text = lookupResult.title
                txtInformation.text = lookupResult.latestChapter
            }
        }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<LookupResult>() {
            override fun areItemsTheSame(oldItem: LookupResult, newItem: LookupResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LookupResult, newItem: LookupResult): Boolean {
                return oldItem == newItem
            }
        }
    }
}