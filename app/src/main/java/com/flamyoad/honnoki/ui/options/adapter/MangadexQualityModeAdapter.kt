package com.flamyoad.honnoki.ui.options.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.MangadexQualityOptionListItemBinding
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.ui.options.model.MangadexQualityModeOption

class MangadexQualityModeAdapter(private val onClick: (MangadexQualityMode) -> Unit)
    : BaseAdapter<MangadexQualityModeOption, MangadexQualityOptionListItemBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MangadexQualityOptionListItemBinding
        get() = MangadexQualityOptionListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: MangadexQualityModeOption) {
        with(holder.binding) {
            val context = holder.itemView.context

            txtMangadexQuality.text = when (item.mode) {
                MangadexQualityMode.DATA -> "Original"
                MangadexQualityMode.DATA_SAVER -> "Compressed"
            }

            if (item.isSelected) {
                val drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_box_24)
                Glide.with(context)
                    .load(drawable)
                    .into(logoTicked)
            } else {
                Glide.with(context).clear(logoTicked)
            }
        }
    }

    override fun onItemClick(item: MangadexQualityModeOption) {
        super.onItemClick(item)
        onClick.invoke(item.mode)
    }
}