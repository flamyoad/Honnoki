package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.honnoki.databinding.BookmarkListItemBinding
import com.flamyoad.honnoki.model.Bookmark

class BookmarkAdapter(private val onBookmarkClick: (Bookmark) -> Unit)
    : BaseListAdapter<Bookmark, BookmarkListItemBinding>(BOOKMARK_COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BookmarkListItemBinding
        get() = BookmarkListItemBinding::inflate

    override fun onCreate(holder: BaseViewHolder, binding: BookmarkListItemBinding) {
        val item = getItem(holder.bindingAdapterPosition)
        holder.itemView.setOnClickListener {
            onBookmarkClick(item)
        }
    }

    override fun onBind(holder: BaseViewHolder, item: Bookmark) {
        with(holder.binding) {
            txtTitle.text = item.mangaOverviewId.toString()
        }
    }

    companion object {
        val BOOKMARK_COMPARATOR = object: DiffUtil.ItemCallback<Bookmark>() {
            override fun areItemsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Bookmark, newItem: Bookmark): Boolean {
                return oldItem == newItem
            }
        }
    }
}