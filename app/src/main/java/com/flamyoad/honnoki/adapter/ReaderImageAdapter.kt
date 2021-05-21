package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.honnoki.databinding.ReaderImageListItemBinding
import com.flamyoad.honnoki.model.Page
import com.flamyoad.honnoki.utils.ViewUtils
import com.flamyoad.honnoki.utils.ui.MangaImageViewTarget

class ReaderImageAdapter: RecyclerView.Adapter<ReaderImageAdapter.ImageViewHolder>() {

    private var list: List<Page> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ReaderImageListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ImageViewHolder(binding)

        binding.btnRetry.setOnClickListener {
            val item = list[holder.bindingAdapterPosition]
            holder.loadImage(item)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.loadImage(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    fun setList(list: List<Page>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(val binding: ReaderImageListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun loadImage(page: Page) {
            binding.errorContainer.isVisible = false
            binding.progressBarContainer.isVisible = true

            binding.txtPageNumber.text = page.number.toString()

            binding.imageView.setOnImageEventListener(object: SubsamplingScaleImageView.DefaultOnImageEventListener() {
                override fun onReady() {
                    super.onReady()
                    binding.statusContainer.isVisible = false
                }
            })

            val urlWithHeader = GlideUrl(page.link) {
                mapOf("Referer" to "https://manganelo.com/")
            }

            Glide.with(binding.root)
                .download(urlWithHeader)
                .into(MangaImageViewTarget(binding))
        }

        fun recycle() {
            binding.imageView.recycle()
        }
    }
}