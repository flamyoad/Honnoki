package com.flamyoad.honnoki.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.MangaRecentListItemBinding
import com.flamyoad.honnoki.data.model.Manga

class RecentMangaListAdapter(private val onItemClick: (Manga) -> Unit) :
    PagingDataAdapter<Manga, RecentMangaListAdapter.MangaViewHolder>(MANGA_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manga_recent_list_item, parent, false)

        val holder = MangaViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val manga = getItem(position) ?: return@setOnClickListener
                onItemClick(manga)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        val manga = getItem(position) ?: return
        holder.bind(manga)
    }

    inner class MangaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = MangaRecentListItemBinding.bind(itemView)

        fun bind(manga: Manga) {
            val loadingIndicator = CircularProgressDrawable(itemView.context).apply {
                setColorSchemeColors(Color.WHITE)
                centerRadius = 50f
                strokeWidth = 5f
            }
            loadingIndicator.start()

            with(binding) {
                Glide.with(itemView.context)
                    .load(manga.coverImage)
                    .placeholder(loadingIndicator)
                    .into(coverImage)

                txtTitle.text = manga.title
                txtInformation.text = manga.latestChapter
            }
        }
    }

    companion object {
        private val MANGA_COMPARATOR = object : DiffUtil.ItemCallback<Manga>() {
            override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                val status = oldItem.id == newItem.id
                return status
            }

            override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                val status = oldItem == newItem
                return status
            }

        }
    }
}