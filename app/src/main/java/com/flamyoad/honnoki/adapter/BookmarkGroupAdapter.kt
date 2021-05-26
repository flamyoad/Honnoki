package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.BookmarkGroupItemStackedBinding
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages

class BookmarkGroupAdapter :
    ListAdapter<BookmarkGroupWithCoverImages, BookmarkGroupAdapter.GroupViewHolder>(GROUP_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding =
            BookmarkGroupItemStackedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = GroupViewHolder(binding)

        return holder
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(binding: BookmarkGroupItemStackedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: BookmarkGroupWithCoverImages) {

        }

        private fun loadImage() {

        }
    }

    companion object {
        val GROUP_COMPARATOR = object : DiffUtil.ItemCallback<BookmarkGroupWithCoverImages>() {
            override fun areItemsTheSame(
                oldItem: BookmarkGroupWithCoverImages,
                newItem: BookmarkGroupWithCoverImages
            ): Boolean {
                return oldItem.bookmarkGroup.id == newItem.bookmarkGroup.id
            }

            override fun areContentsTheSame(
                oldItem: BookmarkGroupWithCoverImages,
                newItem: BookmarkGroupWithCoverImages
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}