package com.flamyoad.honnoki.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.honnoki.adapter.BaseListAdapter
import com.flamyoad.honnoki.databinding.SearchSourceListItemBinding
import com.flamyoad.honnoki.ui.search.model.SearchSource

class SourcePickerAdapter(private val onSourceClick: (SearchSource) -> Unit)
    : BaseListAdapter<SearchSource, SearchSourceListItemBinding>(COMPARATOR) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SearchSourceListItemBinding
        get() = SearchSourceListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: SearchSource) {
        with(holder.binding) {
            chip.text = item.name
        }
    }

    override fun onItemClick(item: SearchSource?) {
        super.onItemClick(item)
        if (item != null) {
            onSourceClick.invoke(item)
        }
    }

    companion object {
        private val COMPARATOR = object: DiffUtil.ItemCallback<SearchSource>() {
            override fun areItemsTheSame(oldItem: SearchSource, newItem: SearchSource): Boolean {
                return oldItem.source == newItem.source
            }

            override fun areContentsTheSame(oldItem: SearchSource, newItem: SearchSource): Boolean {
                return oldItem == newItem
            }
        }
    }
}