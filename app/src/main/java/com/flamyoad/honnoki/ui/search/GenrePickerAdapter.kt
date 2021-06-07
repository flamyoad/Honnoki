package com.flamyoad.honnoki.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.honnoki.adapter.BaseListAdapter
import com.flamyoad.honnoki.databinding.SearchGenreListItemBinding
import com.flamyoad.honnoki.ui.search.model.SearchGenre

class GenrePickerAdapter(private val onGenreClick: (SearchGenre) -> Unit) :
    BaseListAdapter<SearchGenre, SearchGenreListItemBinding>(GENRE_COMPARATOR) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SearchGenreListItemBinding
        get() = SearchGenreListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: SearchGenre) {
        with(holder.binding) {
            chip.text = item.name
            chip.isChecked = item.isSelected
        }
    }

    override fun onItemClick(item: SearchGenre?) {
        super.onItemClick(item)
        item?.let { onGenreClick.invoke(it) }
    }

    companion object {
        val GENRE_COMPARATOR = object : DiffUtil.ItemCallback<SearchGenre>() {
            override fun areItemsTheSame(oldItem: SearchGenre, newItem: SearchGenre): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: SearchGenre, newItem: SearchGenre): Boolean {
                return oldItem == newItem
            }
        }
    }
}