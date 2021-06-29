package com.flamyoad.honnoki.ui.reader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.ReaderImageListLoadingBinding

class ReaderLoadingAdapter: BaseAdapter<Any, ReaderImageListLoadingBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ReaderImageListLoadingBinding
        get() = ReaderImageListLoadingBinding::inflate

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun onBind(holder: BaseViewHolder, item: Any) {}

    override fun getItemCount() = 1
}