package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.GenreListItemBinding
import com.flamyoad.honnoki.model.Genre

class GenreListAdapter()
    : RecyclerView.Adapter<GenreListAdapter.GenreViewHolder>() {

    private var list: List<Genre> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.genre_list_item, parent, false)

        val holder = GenreViewHolder(view)
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

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = GenreListItemBinding.bind(itemView)

        fun bind(genre: Genre) {
            binding.chip.text = genre.name
        }
    }
}