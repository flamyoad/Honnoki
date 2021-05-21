package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.ChapterListItemBinding
import com.flamyoad.honnoki.model.Chapter

class ChapterListAdapter(private val onChapterClick: (Chapter) -> Unit) :
    ListAdapter<Chapter, ChapterListAdapter.ChapterViewHolder>(ChapterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val layout = ChapterListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ChapterViewHolder(layout)
        holder.itemView.setOnClickListener {
            onChapterClick(getItem(holder.bindingAdapterPosition))
        }
        return holder
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChapterViewHolder(private val binding: ChapterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chapter: Chapter) {
            with(binding) {
                txtTitle.text = chapter.title
                txtDate.text = chapter.date
                btnAction.setOnClickListener {  }
            }
        }
    }
}

class ChapterDiffUtil : DiffUtil.ItemCallback<Chapter>() {
    override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem == newItem
    }
}