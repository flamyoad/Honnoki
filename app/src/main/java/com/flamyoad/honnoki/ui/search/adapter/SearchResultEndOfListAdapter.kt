package com.flamyoad.honnoki.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.SimpleSearchEndOfListBinding

class SearchResultEndOfListAdapter: BaseAdapter<Any, SimpleSearchEndOfListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SimpleSearchEndOfListBinding
        get() = SimpleSearchEndOfListBinding::inflate

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun onBind(holder: BaseViewHolder, item: Any) {}

    override fun getItemCount(): Int {
        return 1
    }
}