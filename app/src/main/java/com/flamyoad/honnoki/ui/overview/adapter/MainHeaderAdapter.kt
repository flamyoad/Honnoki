package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.MangaSummaryListHeaderBinding

class MainHeaderAdapter :
    RecyclerView.Adapter<MainHeaderAdapter.HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            MangaSummaryListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = HeaderViewHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {}

    override fun getItemCount(): Int = 1

    inner class HeaderViewHolder(val binding: MangaSummaryListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)
}