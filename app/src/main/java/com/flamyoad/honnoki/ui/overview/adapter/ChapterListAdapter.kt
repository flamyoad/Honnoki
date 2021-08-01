package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.common.adapter.BaseListAdapter
import com.flamyoad.honnoki.databinding.ChapterListItemComplexBinding
import com.flamyoad.honnoki.ui.overview.model.ReaderChapter
import com.flamyoad.honnoki.utils.ColorUtils

class ChapterListAdapter(private val onChapterClick: (ReaderChapter) -> Unit) :
    BaseListAdapter<ReaderChapter, ChapterListItemComplexBinding>(COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ChapterListItemComplexBinding
        get() = ChapterListItemComplexBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: ReaderChapter) {
        val context = holder.binding.root.context

        with(holder.binding) {
            txtChapter.text = item.title
            txtInfo.text = item.translatedLanguage + " | " + item.date
            icon.isVisible = item.hasBeenRead

            val textColor = if (item.currentlyRead) {
                android.R.attr.textColorPrimary
            } else {
                if (item.hasBeenRead) {
                    android.R.attr.textColorSecondary
                } else {
                    android.R.attr.textColorPrimary
                }
            }
            txtChapter.setTextColor(ColorUtils.resolveColorAttr(context, textColor))

            if (item.currentlyRead) {
                val drawable = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_baseline_location_on_24,
                    context.theme
                )
                Glide.with(context)
                    .load(drawable)
                    .into(icon)
            } else {
                Glide.with(context).clear(icon)
            }
        }
    }

    override fun onItemClick(item: ReaderChapter?) {
        super.onItemClick(item)
        item?.let { onChapterClick.invoke(it) }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<ReaderChapter>() {
            override fun areItemsTheSame(oldItem: ReaderChapter, newItem: ReaderChapter): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ReaderChapter,
                newItem: ReaderChapter
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}