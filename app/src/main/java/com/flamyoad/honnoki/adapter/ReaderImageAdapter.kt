package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.honnoki.databinding.ReaderImageListItemBinding
import com.flamyoad.honnoki.model.Page
import com.flamyoad.honnoki.utils.ViewUtils
import com.flamyoad.honnoki.utils.ui.MangaImageViewTarget
import java.util.*

class ReaderImageAdapter: BaseListAdapter<Page, ReaderImageListItemBinding>(PAGE_COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ReaderImageListItemBinding
        get() = ReaderImageListItemBinding::inflate

    override fun onCreate(holder: BaseViewHolder, binding: ReaderImageListItemBinding) {
        binding.btnRetry.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition)
            holder.loadImage(item)
        }
    }

    override fun onBind(holder: BaseViewHolder, page: Page) {
        holder.loadImage(page)
    }

    override fun onRecycleView(holder: BaseViewHolder) {
        super.onRecycleView(holder)
        with(holder.binding) {
            imageView.recycle()
        }
    }

    private fun BaseViewHolder.loadImage(page: Page) {
        with(binding) {
            errorContainer.isVisible = false
            progressBarContainer.isVisible = true

            txtPageNumber.text = page.number.toString()

            imageView.setOnImageEventListener(object: SubsamplingScaleImageView.DefaultOnImageEventListener() {
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

    companion object {
        val PAGE_COMPARATOR = object: DiffUtil.ItemCallback<Page>() {
            override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
                return oldItem == newItem
            }
        }
    }
}