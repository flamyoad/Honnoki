package com.flamyoad.honnoki.ui.home.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.honnoki.common.adapter.BaseListAdapter
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.databinding.MangaGenrePickerListItemBinding

class MangaGenreAdapter(private val onGenreClick: (GenreConstants) -> Unit) :
    BaseListAdapter<GenreConstants, MangaGenrePickerListItemBinding>(COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MangaGenrePickerListItemBinding
        get() = MangaGenrePickerListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: GenreConstants) {
        val name = item.toReadableName(holder.itemView.context)
        holder.binding.txtGenre.text = name
    }

    override fun onItemClick(item: GenreConstants?) {
        super.onItemClick(item)
        onGenreClick.invoke(item ?: return)
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<GenreConstants>() {
            override fun areItemsTheSame(
                oldItem: GenreConstants,
                newItem: GenreConstants
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GenreConstants,
                newItem: GenreConstants
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}