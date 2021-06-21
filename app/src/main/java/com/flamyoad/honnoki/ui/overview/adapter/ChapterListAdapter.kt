package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.ChapterListItemBinding
import com.flamyoad.honnoki.ui.overview.model.ReaderChapter
import com.flamyoad.honnoki.utils.ColorUtils

class ChapterListAdapter(private val onChapterClick: (ReaderChapter) -> Unit) :
    ListAdapter<ReaderChapter, ChapterListAdapter.ChapterViewHolder>(ChapterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val layout =
            ChapterListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ChapterViewHolder(layout)
        holder.itemView.setOnClickListener {
            onChapterClick(getItem(holder.bindingAdapterPosition))
        }
        return holder
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: ChapterViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    inner class ChapterViewHolder(private val binding: ChapterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context get() = binding.root.context

        fun bind(chapter: ReaderChapter) {
            with(binding) {
                txtChapter.text = chapter.title

                if (chapter.currentlyRead) {
                    setCurrentlyReadBackground()
                    return
                }

                if (chapter.hasBeenRead) {
                    setHasBeenReadBackground()
                    return
                }

                setNormalBackground()
            }
        }

        private fun setNormalBackground() {
            Glide.with(context).clear(binding.icon)

            val defaultBackground = ColorUtils.resolveColorAttr(context, R.attr.colorSurface)
            binding.btnChapter.setCardBackgroundColor(defaultBackground)

            val textColor = ColorUtils.resolveColorAttr(context, android.R.attr.textColorPrimary)
            binding.txtChapter.setTextColor(textColor)
        }

        private fun setCurrentlyReadBackground() {
            val drawable = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_baseline_location_on_24,
                context.theme
            )
            Glide.with(context)
                .load(drawable)
                .into(binding.icon)

            val backgroundColor = ContextCompat.getColor(context, R.color.chapter_last_read)
            binding.btnChapter.setCardBackgroundColor(backgroundColor)

            val textColor = ColorUtils.resolveColorAttr(context, android.R.attr.textColorPrimary)
            binding.txtChapter.setTextColor(textColor)
        }

        private fun setHasBeenReadBackground() {
            Glide.with(context).clear(binding.icon)

            val defaultBackground = ColorUtils.resolveColorAttr(context, R.attr.colorSurface)
            binding.btnChapter.setCardBackgroundColor(defaultBackground)

            val textColor = ContextCompat.getColor(context, R.color.subLightTextColor)
            binding.txtChapter.setTextColor(textColor)
        }

        fun recycle() {
            Glide.with(context).clear(binding.icon)

            val defaultBackground = ColorUtils.resolveColorAttr(context, R.attr.colorSurface)
            binding.btnChapter.setCardBackgroundColor(defaultBackground)
        }
    }
}

class ChapterDiffUtil : DiffUtil.ItemCallback<ReaderChapter>() {
    override fun areItemsTheSame(oldItem: ReaderChapter, newItem: ReaderChapter): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ReaderChapter, newItem: ReaderChapter): Boolean {
        return oldItem == newItem
    }
}