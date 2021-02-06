package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.SourceSwitcherListItemBinding
import com.flamyoad.honnoki.model.Source

class SourceSwitcherAdapter(
    private val itemList: List<Source>,
    private val onItemClick: (Source) -> Unit
) : RecyclerView.Adapter<SourceSwitcherAdapter.SourceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.source_switcher_list_item, parent, false)

        val holder = SourceViewHolder(view)

        holder.itemView.setOnClickListener {
            val source = itemList[holder.bindingAdapterPosition]
            onItemClick(source)
        }

        return holder
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class SourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = SourceSwitcherListItemBinding.bind(itemView)

        fun bind(source: Source) {
            val sourceLogo = source.getLogoDrawable(itemView.context)

            val languageLogo = source.getLanguageDrawable(itemView.context)

            with(binding) {
                Glide.with(itemView.context)
                    .load(sourceLogo)
                    .into(imageSource)

                Glide.with(itemView.context)
                    .load(languageLogo)
                    .into(imageLanguage)

                txtName.text = source.title
            }
        }
    }
}