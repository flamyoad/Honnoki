package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.ChapterListLoadingLayoutBinding

class ChapterListLoadingAdapter: BaseAdapter<Any, ChapterListLoadingLayoutBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ChapterListLoadingLayoutBinding
        get() = ChapterListLoadingLayoutBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: Any) {}

    override fun getItemCount(): Int = 1
}