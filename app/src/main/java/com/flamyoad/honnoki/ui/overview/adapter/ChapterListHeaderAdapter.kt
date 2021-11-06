package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.ChapterListSorterItemBinding

class ChapterListHeaderAdapter(
    private val sortList: () -> Unit,
    private val changeListType: () -> Unit,
) :
    RecyclerView.Adapter<ChapterListHeaderAdapter.SorterViewHolder>() {

    private var totalChapters: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SorterViewHolder {
        val binding =
            ChapterListSorterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = SorterViewHolder(binding)

        binding.btnSort.setOnClickListener {
            sortList.invoke()
        }
        binding.btnListType.setOnClickListener {
            changeListType.invoke()
        }
        return holder
    }

    override fun onBindViewHolder(holder: SorterViewHolder, position: Int) {
        holder.bind(totalChapters)
    }

    fun setItem(totalChapters: Int) {
        this.totalChapters = totalChapters
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = 1

    inner class SorterViewHolder(val binding: ChapterListSorterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(totalChapters: Int) {
            val resources = binding.root.context.resources
            binding.txtTotalChapters.text = if (totalChapters == 0) {
                resources.getString(R.string.chapter_list_no_items)
            } else {
                resources.getQuantityString(
                    R.plurals.chapter_list_total,
                    totalChapters,
                    totalChapters
                )
            }
        }
    }
}