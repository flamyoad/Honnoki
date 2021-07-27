package com.flamyoad.honnoki.ui.reader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.common.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.LayoutEndOfChapterListBinding

class EndOfChapterListAdapter: BaseAdapter<Any, LayoutEndOfChapterListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutEndOfChapterListBinding
        get() = LayoutEndOfChapterListBinding::inflate

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun onBind(holder: BaseViewHolder, item: Any) {}

    override fun getItemCount(): Int = 1
}