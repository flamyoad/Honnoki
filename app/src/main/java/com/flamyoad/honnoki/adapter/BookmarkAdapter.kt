package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.databinding.BookmarkListItemBinding
import com.flamyoad.honnoki.model.Bookmark
import com.flamyoad.honnoki.model.BookmarkWithOverview

class BookmarkAdapter(private val onBookmarkClick: (BookmarkWithOverview) -> Unit) :
    BaseListAdapter<BookmarkWithOverview, BookmarkListItemBinding>(BOOKMARK_COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BookmarkListItemBinding
        get() = BookmarkListItemBinding::inflate

    override fun onCreate(holder: BaseViewHolder, binding: BookmarkListItemBinding) {
        holder.itemView.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition) ?: return@setOnClickListener
            onBookmarkClick(item)
        }
    }

    override fun onBind(holder: BaseViewHolder, item: BookmarkWithOverview) {
        with(holder.binding) {
            Glide.with(this.root)
                .load(item.overview.coverImage)
                .into(coverImage)

            txtTitle.text = item.overview.mainTitle
            txtInformation.text = item.overview.status
        }
    }

    companion object {
        val BOOKMARK_COMPARATOR = object : DiffUtil.ItemCallback<BookmarkWithOverview>() {
            override fun areItemsTheSame(
                oldItem: BookmarkWithOverview,
                newItem: BookmarkWithOverview
            ): Boolean {
                return oldItem.bookmark.id == newItem.bookmark.id
            }

            override fun areContentsTheSame(
                oldItem: BookmarkWithOverview,
                newItem: BookmarkWithOverview
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}