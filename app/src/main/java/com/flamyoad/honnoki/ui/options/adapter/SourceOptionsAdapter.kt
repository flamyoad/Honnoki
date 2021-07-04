package com.flamyoad.honnoki.ui.options.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.SourceOptionListItemBinding
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.options.model.SourceOption

class SourceOptionsAdapter(private val onSourceClick: (Source) -> Unit) :
    BaseAdapter<SourceOption, SourceOptionListItemBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SourceOptionListItemBinding
        get() = SourceOptionListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: SourceOption) {
        with(holder.binding) {
            val context = holder.itemView.context

            txtSource.text = item.source.title
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

    override fun onItemClick(item: SourceOption) {
        super.onItemClick(item)
        val source = item.source
        onSourceClick.invoke(source)
    }
}