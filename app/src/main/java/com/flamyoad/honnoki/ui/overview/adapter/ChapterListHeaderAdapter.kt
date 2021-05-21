package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.ChapterListSorterItemBinding
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.State

class ChapterListHeaderAdapter(val sortList: (Boolean) -> Unit) :
    RecyclerView.Adapter<ChapterListHeaderAdapter.SorterViewHolder>() {

    private var chapterListState: State<List<Chapter>> = State.Loading

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
        holder.bind(chapterListState)
    }

    fun setItem(chapterListState: State<List<Chapter>>) {
        this.chapterListState = chapterListState
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = 1

    inner class SorterViewHolder(val binding: ChapterListSorterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chapterListState: State<List<Chapter>>) {
            val resources = binding.root.context.resources
            when (chapterListState) {
                is State.Success -> {
                    binding.txtTotalChapters.text = resources.getString(
                        R.string.chapter_list_total,
                        chapterListState.value.size
                    )
                }
                is State.Error -> {
                    binding.txtTotalChapters.text = resources.getString(R.string.chapter_list_total,0)
                }
            }
        }
    }
}