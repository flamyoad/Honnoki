package com.flamyoad.honnoki.ui.library.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.cache.CoverCache
import com.flamyoad.honnoki.data.entities.ReadHistory
import com.flamyoad.honnoki.databinding.ReadHistoryListHeaderBinding
import com.flamyoad.honnoki.databinding.ReadHistoryListItemBinding
import com.flamyoad.honnoki.ui.library.history.model.ViewReadHistory
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ReadHistoryAdapter(
    private val coverCache: CoverCache,
    private val onItemClick: (ReadHistory) -> Unit,
    private val onResumeRead: (ReadHistory) -> Unit,
    private val onRemoveItem: (ReadHistory) -> Unit,
) : PagingDataAdapter<ViewReadHistory, RecyclerView.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> {
                val binding = ReadHistoryListHeaderBinding.inflate(layoutInflater, parent, false)
                val holder = HeaderViewHolder(binding)
                holder
            }
            ITEM -> {
                val binding = ReadHistoryListItemBinding.inflate(layoutInflater, parent, false)
                val holder = ItemViewHolder(binding)

                binding.rootLayout.setOnClickListener {
                    val item = getItem(holder.bindingAdapterPosition) as? ViewReadHistory.Item
                        ?: return@setOnClickListener
                    onItemClick(item.history)
                }

                binding.btnRead.setOnClickListener {
                    val item = getItem(holder.bindingAdapterPosition) as? ViewReadHistory.Item
                        ?: return@setOnClickListener
                    onResumeRead(item.history)
                }

                binding.btnDelete.setOnClickListener {
                    val item = getItem(holder.bindingAdapterPosition) as? ViewReadHistory.Item
                        ?: return@setOnClickListener
                    onRemoveItem(item.history)
                }
                holder
            }
            else -> throw IllegalArgumentException("Invalid view holder")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is ViewReadHistory.Header -> {
                (holder as HeaderViewHolder).bind(item.date)
            }
            is ViewReadHistory.Item -> {
                (holder as ItemViewHolder).bind(item.history)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ViewReadHistory.Header -> HEADER
            is ViewReadHistory.Item -> ITEM
            else -> throw NotImplementedError("")
        }
    }

    inner class ItemViewHolder(val binding: ReadHistoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReadHistory) {
            val chapter = item.chapter

            with(binding) {
                Glide.with(root)
                    .load(coverCache.get(item.overview.coverImage))
                    .into(binding.coverImage)

                txtTitle.text = item.overview.mainTitle
                txtLatestChapter.text = chapter.title
                txtLastReadTime.text = timeFormatter.format(item.overview.lastReadDateTime)
            }
        }
    }

    inner class HeaderViewHolder(val binding: ReadHistoryListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: LocalDate) {
            binding.txtHeader.text = dateFormatter.format(date)
        }
    }

    companion object {
        private const val HEADER = 0
        private const val ITEM = 1

        private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        private val timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a")

        val COMPARATOR = object : DiffUtil.ItemCallback<ViewReadHistory>() {
            override fun areItemsTheSame(
                oldItem: ViewReadHistory,
                newItem: ViewReadHistory
            ): Boolean {
                if (oldItem is ViewReadHistory.Item && newItem is ViewReadHistory.Item) {
                    return oldItem.history.overview.id!! == newItem.history.overview.id!!
                }
                if (oldItem is ViewReadHistory.Header && newItem is ViewReadHistory.Header) {
                    return oldItem.date == newItem.date
                }
                return false
            }

            override fun areContentsTheSame(
                oldItem: ViewReadHistory,
                newItem: ViewReadHistory
            ): Boolean {
                if (oldItem is ViewReadHistory.Item && newItem is ViewReadHistory.Item) {
                    return oldItem == newItem
                }
                if (oldItem is ViewReadHistory.Header && newItem is ViewReadHistory.Header) {
                    return oldItem == newItem
                }
                return false
            }
        }
    }
}