package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.databinding.BookmarkGroupItemStackedBinding
import com.flamyoad.honnoki.databinding.ButtonAddBookmarkGroupBinding
import com.flamyoad.honnoki.data.model.BookmarkGroup
import com.flamyoad.honnoki.data.model.BookmarkGroupWithInfo

private const val BTN_ADD_NEW_GROUP = 0
private const val GROUP_ITEM = 1

class BookmarkGroupAdapter(
    private val onBookmarkGroupClick: (BookmarkGroup) -> Unit,
    private val onAddButtonClick: () -> Unit) :
    ListAdapter<BookmarkGroupWithInfo, RecyclerView.ViewHolder>(GROUP_COMPARATOR) {

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
        return if (item == BookmarkGroupWithInfo.empty()) {
            BTN_ADD_NEW_GROUP
        } else {
            GROUP_ITEM
        }
    }

    override fun submitList(list: List<BookmarkGroupWithInfo>?) {
        // Add empty object at first position. It acts as the "Add New Group" button
        // We don't want to modify getItemCount() to return + 1 because it will mess up Diffutil
        val modifiedList = list?.toMutableList()
        modifiedList?.add(0, BookmarkGroupWithInfo.empty())
        super.submitList(modifiedList)
    }

    inner class GroupViewHolder(val binding: BookmarkGroupItemStackedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BookmarkGroupWithInfo) {
            binding.txtGroupName.text = item.bookmarkGroup.name
            binding.txtTotalManga.text = "(" + item.itemCount + ")"
            loadCoverImages(item.coverImages)
        }

        private fun loadCoverImages(coverImages: List<String>) {
            for (i in 0 until BookmarkGroup.COVER_IMAGE_LIMIT) {
                val imageView = when (i) {
                    0 -> binding.firstImage
                    1 -> binding.secondImage
                    2 -> binding.thirdImage
                    else -> throw IllegalArgumentException("Image does not exist")
                }

                Glide.with(imageView)
                    .load(coverImages.getOrNull(i))
                    .into(imageView)
            }
        }
    }

    inner class AddButtonViewHolder(val binding: ButtonAddBookmarkGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        val GROUP_COMPARATOR = object : DiffUtil.ItemCallback<BookmarkGroupWithInfo>() {
            override fun areItemsTheSame(
                oldItem: BookmarkGroupWithInfo,
                newItem: BookmarkGroupWithInfo
            ): Boolean {
                return oldItem.bookmarkGroup.id == newItem.bookmarkGroup.id
            }

            override fun areContentsTheSame(
                oldItem: BookmarkGroupWithInfo,
                newItem: BookmarkGroupWithInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}