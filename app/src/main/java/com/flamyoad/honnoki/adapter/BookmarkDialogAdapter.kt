package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.honnoki.databinding.DialogBookmarkGroupListItemBinding
import com.flamyoad.honnoki.model.BookmarkGroup

class BookmarkDialogAdapter(private val onGroupClick: (BookmarkGroup) -> Unit)
    : BaseListAdapter<BookmarkGroup, DialogBookmarkGroupListItemBinding>(COMPARATOR) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogBookmarkGroupListItemBinding
        get() = DialogBookmarkGroupListItemBinding::inflate

    override fun onCreate(holder: BaseViewHolder, binding: DialogBookmarkGroupListItemBinding) {
        binding.checkBox.setOnClickListener {
            binding.rootLayout.performClick()
        }
    }

    override fun onItemClick(item: BookmarkGroup?) {
        super.onItemClick(item)
        onGroupClick(item ?: return)
    }

    override fun onBind(holder: BaseViewHolder, item: BookmarkGroup) {
        with(holder.binding) {
            txtGroupName.text = item.name
            checkBox.isChecked = item.isSelected
            txtIcon.text = item.name.first().toUpperCase().toString()
        }
    }

    companion object {
        val COMPARATOR = object: DiffUtil.ItemCallback<BookmarkGroup>() {
            override fun areItemsTheSame(oldItem: BookmarkGroup, newItem: BookmarkGroup): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BookmarkGroup, newItem: BookmarkGroup): Boolean {
                return oldItem == newItem
            }
        }
    }
}