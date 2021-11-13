package com.flamyoad.honnoki.ui.reader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.honnoki.data.entities.Page
import com.flamyoad.honnoki.databinding.ReaderImageListAdsBinding
import com.flamyoad.honnoki.databinding.ReaderImageListItemBinding
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import com.flamyoad.honnoki.utils.ui.MangaImageViewTarget

class ScrollingImageAdapter(
    private val source: Source,
    private val quality: MangadexQualityMode
) : ListAdapter<ReaderPage, RecyclerView.ViewHolder>(PAGE_COMPARATOR) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM -> {
                val binding = ReaderImageListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                val holder = ItemViewHolder(binding)
                binding.btnRetry.setOnClickListener {
                    val item =
                        getItem(holder.bindingAdapterPosition) as ReaderPage.Value
                    holder.loadImage(item.page)
                }
                holder
            }

            ADS -> {
                val binding = ReaderImageListAdsBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                val holder = AdsViewHolder(binding)
                holder
            }

            else -> throw IllegalArgumentException("Invalid view type in onCreateViewHolder!!")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        when (holder) {
            is ItemViewHolder -> {
                val pageWithChapterInfo = (item as ReaderPage.Value)
                holder.loadImage(pageWithChapterInfo.page)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ReaderPage.Value -> ITEM
            is ReaderPage.Ads -> ADS
            else -> throw IllegalArgumentException("Invalid reader item view type")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is ItemViewHolder -> {
                holder.binding.imageView.recycle()
            }
        }
    }

    inner class ItemViewHolder(val binding: ReaderImageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun loadImage(page: Page) {
            with(binding) {
                errorContainer.isVisible = false
                progressBarContainer.isVisible = true

                txtPageNumber.text = page.number.toString()

                imageView.apply {
                    maxScale = 10f // Removes black strip around small image
                    setOnImageEventListener(object :
                        SubsamplingScaleImageView.DefaultOnImageEventListener() {
                        override fun onReady() {
                            super.onReady()
                            statusContainer.isVisible = false
                        }
                    })
                }

                Glide.with(root)
                    .download(getImageUrl(page))
                    .timeout(15000) // 15 seconds
                    .into(MangaImageViewTarget(this))
            }
        }

        private fun getImageUrl(page: Page): GlideUrl {
            val url = when {
                source == Source.MANGAKALOT -> {
                    GlideUrl(page.link) { mapOf("Referer" to "https://manganelo.com/") }
                }
                source == Source.MANGADEX && quality == MangadexQualityMode.DATA_SAVER -> {
                    GlideUrl(page.linkDataSaver ?: page.link)
                }
                else -> {
                    GlideUrl(page.link)
                }
            }
            return url
        }
    }

    inner class AdsViewHolder(val binding: ReaderImageListAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    companion object {
        const val ITEM = 0
        const val ADS = 1

        val PAGE_COMPARATOR = object : DiffUtil.ItemCallback<ReaderPage>() {
            override fun areItemsTheSame(
                oldItem: ReaderPage,
                newItem: ReaderPage
            ): Boolean {
                if (oldItem is ReaderPage.Value && newItem is ReaderPage.Value) {
                    return oldItem.page.id == newItem.page.id
                }
                if (oldItem is ReaderPage.Ads && newItem is ReaderPage.Ads) {
                    return oldItem.chapterId == newItem.chapterId
                }
                return false
            }

            override fun areContentsTheSame(
                oldItem: ReaderPage,
                newItem: ReaderPage
            ): Boolean {
                if (oldItem is ReaderPage.Value && newItem is ReaderPage.Value) {
                    return oldItem.pageWithChapterInfo == newItem.pageWithChapterInfo
                }
                if (oldItem is ReaderPage.Ads && newItem is ReaderPage.Ads) {
                    return oldItem.chapterId == newItem.chapterId
                }
                return false
            }
        }
    }
}