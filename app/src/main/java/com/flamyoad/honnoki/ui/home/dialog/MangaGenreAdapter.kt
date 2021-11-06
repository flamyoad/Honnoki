package com.flamyoad.honnoki.ui.home.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.honnoki.common.adapter.BaseListAdapter
import com.flamyoad.honnoki.data.DynamicGenre
import com.flamyoad.honnoki.databinding.MangaGenrePickerListItemBinding

class MangaGenreAdapter(private val onGenreClick: (DynamicGenre) -> Unit) :
    BaseListAdapter<DynamicGenre, MangaGenrePickerListItemBinding>(COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MangaGenrePickerListItemBinding
        get() = MangaGenrePickerListItemBinding::inflate

    override fun onBind(holder: BaseViewHolder, item: DynamicGenre) {
        holder.binding.txtGenre.text = item.name
    }

    override fun onItemClick(item: DynamicGenre?) {
        super.onItemClick(item)
        onGenreClick.invoke(item ?: return)
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<DynamicGenre>() {
            override fun areItemsTheSame(
                oldItem: DynamicGenre,
                newItem: DynamicGenre
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DynamicGenre,
                newItem: DynamicGenre
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}