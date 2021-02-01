package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.MangaListItemBinding
import com.flamyoad.honnoki.model.Manga

class MangaAdapter(private val onItemClick: (Manga) -> Unit) :
    PagingDataAdapter<Manga, MangaAdapter.MangaViewHolder>(MANGA_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manga_list_item, parent, false)

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
        val binding = MangaListItemBinding.bind(itemView)

        fun bind(manga: Manga) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(manga.coverImage)
                    .into(coverImage)

                txtTitle.text = manga.title
                txtInformation.text = manga.latestChapter
            }
        }
    }

    companion object {
        private val MANGA_COMPARATOR = object : DiffUtil.ItemCallback<Manga>() {
            override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem == newItem
            }

        }
    }
}