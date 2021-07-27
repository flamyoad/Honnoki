package com.flamyoad.honnoki.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.common.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.MangaListDividerBinding

class ListDividerAdapter: BaseAdapter<Any, MangaListDividerBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MangaListDividerBinding
        get() = MangaListDividerBinding::inflate

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun getItemCount(): Int = 1

    override fun onBind(holder: BaseViewHolder, item: Any) {}
}