package com.flamyoad.honnoki.ui.library.bookmark.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.common.adapter.BaseListAdapter
import com.flamyoad.honnoki.cache.CoverCache
import com.flamyoad.honnoki.databinding.BookmarkListItemBinding
import com.flamyoad.honnoki.data.entities.BookmarkWithOverview

class BookmarkAdapter(
    private val coverCache: CoverCache,
    private val onBookmarkClick: (BookmarkWithOverview) -> Unit,
    private val onBookmarkLongClick: (BookmarkWithOverview) -> Unit
) : BaseListAdapter<BookmarkWithOverview, BookmarkListItemBinding>(BOOKMARK_COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BookmarkListItemBinding
        get() = BookmarkListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: BookmarkWithOverview) {
        with(holder.binding) {
            Glide.with(this.root)
                .load(coverCache.get(item.overview.coverImage))
                .into(coverImage)

            txtTitle.text = item.overview.mainTitle
            txtInformation.text = item.overview.status

            when (item.isSelected) {
                true -> tickLogo.setImageResource(R.drawable.ic_check)
                false -> tickLogo.setImageDrawable(null)
            }
        }
    }

    override fun onItemClick(item: BookmarkWithOverview?) {
        super.onItemClick(item)
        onBookmarkClick(item ?: return)
    }

    override fun onItemLongClick(item: BookmarkWithOverview?) {
        super.onItemLongClick(item)
        onBookmarkLongClick(item ?: return)
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