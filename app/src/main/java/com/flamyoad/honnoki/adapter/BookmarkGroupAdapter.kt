package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.BookmarkGroupItemStackedBinding
import com.flamyoad.honnoki.databinding.ButtonAddBookmarkGroupBinding
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages

private const val BTN_ADD_NEW_GROUP = 0
private const val GROUP_ITEM = 1

class BookmarkGroupAdapter(
    private val onBookmarkGroupClick: (BookmarkGroup) -> Unit,
    private val onAddButtonClick: () -> Unit) :
    ListAdapter<BookmarkGroupWithCoverImages, RecyclerView.ViewHolder>(GROUP_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            BTN_ADD_NEW_GROUP -> {
                val binding = ButtonAddBookmarkGroupBinding.inflate(layoutInflater, parent, false)

                binding.rootLayout.setOnClickListener {
                    onAddButtonClick()
                }

                binding.btnAdd.setOnClickListener {
                    binding.rootLayout.performClick()
                }

                AddButtonViewHolder(binding)
            }
            GROUP_ITEM -> {
                val binding = BookmarkGroupItemStackedBinding.inflate(LayoutInflater.from(parent.context), parent,false)

                val holder = GroupViewHolder(binding)

                binding.rootLayout.setOnClickListener {
                    val item = getItem(holder.bindingAdapterPosition)
                    onBookmarkGroupClick(item.bookmarkGroup)
                }

                binding.imageLayout.setOnClickListener {
                    binding.rootLayout.performClick()
                }

                holder
            }
            else -> throw IllegalArgumentException("Invalid view type for this adapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            GROUP_ITEM -> (holder as GroupViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item == BookmarkGroupWithCoverImages.empty()) {
            BTN_ADD_NEW_GROUP
        } else {
            GROUP_ITEM
        }
    }

    override fun submitList(list: List<BookmarkGroupWithCoverImages>?) {
        // Add empty object at first position. It acts as the "Add New Group" button
        // We don't want to modify getItemCount() to return + 1 because it will mess up Diffutil
        val modifiedList = list?.toMutableList()
        modifiedList?.add(0, BookmarkGroupWithCoverImages.empty())
        super.submitList(modifiedList)
    }

    inner class GroupViewHolder(val binding: BookmarkGroupItemStackedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BookmarkGroupWithCoverImages) {
            binding.txtGroupName.text = item.bookmarkGroup.name
            binding.txtTotalManga.text = "(" + item.itemCount + ")"
        }

        private fun loadImage() {

        }
    }

    inner class AddButtonViewHolder(val binding: ButtonAddBookmarkGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val GROUP_COMPARATOR = object : DiffUtil.ItemCallback<BookmarkGroupWithCoverImages>() {
            override fun areItemsTheSame(
                oldItem: BookmarkGroupWithCoverImages,
                newItem: BookmarkGroupWithCoverImages
            ): Boolean {
                return oldItem.bookmarkGroup.id == newItem.bookmarkGroup.id
            }

            override fun areContentsTheSame(
                oldItem: BookmarkGroupWithCoverImages,
                newItem: BookmarkGroupWithCoverImages
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}