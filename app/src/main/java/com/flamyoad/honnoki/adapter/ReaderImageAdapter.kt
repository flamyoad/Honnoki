package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.honnoki.databinding.ReaderImageListAdsBinding
import com.flamyoad.honnoki.databinding.ReaderImageListItemBinding
import com.flamyoad.honnoki.databinding.ReaderImageListLoadingBinding
import com.flamyoad.honnoki.model.Page
import com.flamyoad.honnoki.ui.reader.ReaderPage
import com.flamyoad.honnoki.utils.ui.MangaImageViewTarget

class ReaderImageAdapter : ListAdapter<ReaderPage, RecyclerView.ViewHolder>(PAGE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM -> {
                val binding = ReaderImageListItemBinding.inflate(layoutInflater, parent, false)
                val holder = ItemViewHolder(binding)
                binding.btnRetry.setOnClickListener {
                    val item = getItem(holder.bindingAdapterPosition) as ReaderPage.Value
                    holder.loadImage(item.page)
                }
                holder
            }

            ADS -> {
                val binding = ReaderImageListAdsBinding.inflate(layoutInflater, parent, false)
                val holder = AdsViewHolder(binding)
                holder
            }

            LOADING_INDICATOR -> {
                val binding = ReaderImageListLoadingBinding.inflate(layoutInflater, parent, false)
                val holder = LoadingViewHolder(binding)
                holder
            }

            else -> throw IllegalArgumentException("Invalid view type in onCreateViewHolder!!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is ItemViewHolder -> {
                val imagePage = (item as ReaderPage.Value).page
                holder.loadImage(imagePage)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ReaderPage.Value -> ITEM
            is ReaderPage.Ads -> ADS
            is ReaderPage.LoadingIndicator -> LOADING_INDICATOR
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

                imageView.setOnImageEventListener(object :
                    SubsamplingScaleImageView.DefaultOnImageEventListener() {
                    override fun onReady() {
                        super.onReady()
                        statusContainer.isVisible = false
                    }
                })

                val urlWithHeader = GlideUrl(page.link) {
                    mapOf("Referer" to "https://manganelo.com/")
                }

                Glide.with(root)
                    .download(urlWithHeader)
                    .into(MangaImageViewTarget(this))
            }
        }
    }

    inner class AdsViewHolder(val binding: ReaderImageListAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    inner class LoadingViewHolder(val binding: ReaderImageListLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    companion object {
        const val ITEM = 0
        const val ADS = 1
        const val LOADING_INDICATOR = 2

        val PAGE_COMPARATOR = object : DiffUtil.ItemCallback<ReaderPage>() {
            override fun areItemsTheSame(oldItem: ReaderPage, newItem: ReaderPage): Boolean {
                if (oldItem is ReaderPage.Value && newItem is ReaderPage.Value) {
                    return oldItem.page.id == newItem.page.id
                }
                if (oldItem is ReaderPage.Ads && newItem is ReaderPage.Ads) {
                    return oldItem.chapterId == newItem.chapterId
                }
                if (oldItem is ReaderPage.LoadingIndicator && newItem is ReaderPage.LoadingIndicator) {
                    return true
                }
                return false
            }

            override fun areContentsTheSame(oldItem: ReaderPage, newItem: ReaderPage): Boolean {
                if (oldItem is ReaderPage.Value && newItem is ReaderPage.Value) {
                    return oldItem.page == newItem.page
                }
                if (oldItem is ReaderPage.Ads && newItem is ReaderPage.Ads) {
                    return oldItem.chapterId == newItem.chapterId
                }
                if (oldItem is ReaderPage.LoadingIndicator && newItem is ReaderPage.LoadingIndicator) {
                    return true
                }
                return false
            }
        }
    }
}