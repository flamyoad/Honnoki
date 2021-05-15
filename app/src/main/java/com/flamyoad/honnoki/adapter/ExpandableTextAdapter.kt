package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.ExpandableTextviewItemBinding

class ExpandableTextAdapter : RecyclerView.Adapter<ExpandableTextAdapter.TextViewHolder>() {
    private var text: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val binding = ExpandableTextviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val holder = TextViewHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(text)
    }

    override fun getItemCount(): Int = 1

    fun setText(text: String) {
        this.text = text
        notifyDataSetChanged()
    }

    inner class TextViewHolder(private val binding: ExpandableTextviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.expandableTextView.text = text
        }
    }
}