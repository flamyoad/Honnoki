package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.common.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.ChapterListEmptyIndicatorBinding

class ChapterListEmptyAdapter : BaseAdapter<Any, ChapterListEmptyIndicatorBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ChapterListEmptyIndicatorBinding
        get() = ChapterListEmptyIndicatorBinding::inflate

    override fun getItemCount(): Int = 1

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun onBind(holder: BaseViewHolder, item: Any) {}
}