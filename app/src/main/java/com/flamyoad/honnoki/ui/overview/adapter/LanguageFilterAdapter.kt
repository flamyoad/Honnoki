package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.common.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.ChapterListLanguageFilterBinding

class LanguageFilterAdapter
    : BaseAdapter<String, ChapterListLanguageFilterBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ChapterListLanguageFilterBinding
        get() = ChapterListLanguageFilterBinding::inflate

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun getItemCount(): Int = 1

    override fun onBind(holder: BaseViewHolder, item: String) {
        TODO("Not yet implemented")
    }
}