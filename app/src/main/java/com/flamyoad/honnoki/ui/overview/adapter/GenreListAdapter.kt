package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.GenreListItemBinding
import com.flamyoad.honnoki.data.entities.Genre

class GenreListAdapter(private val onGenreClick: (Genre) -> Unit) :
    RecyclerView.Adapter<GenreListAdapter.GenreViewHolder>() {

    private var list: List<Genre> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = GenreListItemBinding.inflate(inflater, parent, false)
        val holder = GenreViewHolder(binding)

        binding.chip.setOnClickListener {
            val item = list[holder.bindingAdapterPosition]
            onGenreClick.invoke(item)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setList(list: List<Genre>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class GenreViewHolder(val binding: GenreListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre) {
            binding.chip.text = genre.name
        }
    }
}