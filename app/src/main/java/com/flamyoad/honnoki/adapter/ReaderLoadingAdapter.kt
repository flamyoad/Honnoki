package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.databinding.ReaderImageListLoadingBinding

class ReaderLoadingAdapter: BaseAdapter<Any, ReaderImageListLoadingBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ReaderImageListLoadingBinding
        get() = ReaderImageListLoadingBinding::inflate

    override fun onCreate(holder: BaseViewHolder, binding: ReaderImageListLoadingBinding) {}

    override fun onBind(holder: BaseViewHolder, item: Any) {}

    override fun getItemCount() = 1
}