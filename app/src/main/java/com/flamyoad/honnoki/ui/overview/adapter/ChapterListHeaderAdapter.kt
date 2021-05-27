package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.ChapterListSorterItemBinding
import com.flamyoad.honnoki.model.Chapter

class ChapterListHeaderAdapter(val sortList: (Boolean) -> Unit) :
    RecyclerView.Adapter<ChapterListHeaderAdapter.SorterViewHolder>() {

    private var chapterList: List<Chapter> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SorterViewHolder {
        val binding =
            ChapterListSorterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = SorterViewHolder(binding)

        binding.btnSort.setOnClickListener {
            sortList(true)
        }

        return holder
    }

    override fun onBindViewHolder(holder: SorterViewHolder, position: Int) {
        holder.bind(chapterList)
    }

    fun setItem(chapterList: List<Chapter>) {
        this.chapterList = chapterList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = 1

    inner class SorterViewHolder(val binding: ChapterListSorterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chapterList: List<Chapter>) {
            val resources = binding.root.context.resources
            if (chapterList.isEmpty()) {
                binding.txtTotalChapters.text = resources.getString(R.string.chapter_list_total, 0)
            } else {
                binding.txtTotalChapters.text = resources.getString(
                    R.string.chapter_list_total, chapterList.size
                )
            }
        }
    }
}