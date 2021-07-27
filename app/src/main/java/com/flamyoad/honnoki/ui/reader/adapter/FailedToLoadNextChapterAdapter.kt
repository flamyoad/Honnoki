package com.flamyoad.honnoki.ui.reader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.common.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.LayoutFailLoadNextChapterBinding

class FailedToLoadNextChapterAdapter(private val reloadNextChapter: () -> Unit) :
    BaseAdapter<Any, LayoutFailLoadNextChapterBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutFailLoadNextChapterBinding
        get() = LayoutFailLoadNextChapterBinding::inflate

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun onCreate(holder: BaseViewHolder, binding: LayoutFailLoadNextChapterBinding) {
        super.onCreate(holder, binding)
        binding.btnRetry.setOnClickListener {
            reloadNextChapter()
        }
    }

    override fun onBind(holder: BaseViewHolder, item: Any) {}

    override fun getItemCount(): Int {
        return 1
    }
}