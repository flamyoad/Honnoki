package com.flamyoad.honnoki.ui.download.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.common.adapter.BaseListAdapter
import com.flamyoad.honnoki.databinding.ChapterListItemBinding
import com.flamyoad.honnoki.ui.download.model.DownloadChapter
import com.flamyoad.honnoki.utils.ColorUtils

class DownloadChapterGridAdapter(
    private val onChapterClick: (DownloadChapter) -> Unit,
    private val onChapterLongClick: (DownloadChapter) -> Unit,
) :
    BaseListAdapter<DownloadChapter, ChapterListItemBinding>(COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ChapterListItemBinding
        get() = ChapterListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: DownloadChapter) {
        with(holder.binding) {
            txtChapter.text = item.title

            val backgroundColor = if (item.isSelected) {
                ContextCompat.getColor(holder.context, R.color.chapter_last_read)
            } else {
                ColorUtils.resolveColorAttr(holder.context, R.attr.colorSurface)
            }
            btnChapter.setCardBackgroundColor(backgroundColor)
        }
    }

    override fun onItemClick(item: DownloadChapter?) {
        super.onItemClick(item)
        onChapterClick.invoke(item ?: return)
    }

    override fun onItemLongClick(item: DownloadChapter?) {
        super.onItemLongClick(item)
        onChapterLongClick.invoke(item ?: return)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<DownloadChapter>() {
            override fun areItemsTheSame(
                oldItem: DownloadChapter,
                newItem: DownloadChapter
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DownloadChapter,
                newItem: DownloadChapter
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}