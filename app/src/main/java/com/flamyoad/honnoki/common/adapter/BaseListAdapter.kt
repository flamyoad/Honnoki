package com.flamyoad.honnoki.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T: Any, VB: ViewBinding>
    (comparator: DiffUtil.ItemCallback<T>): ListAdapter<T, BaseListAdapter<T, VB>.BaseViewHolder>(comparator) {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    abstract fun onBind(holder: BaseViewHolder, item: T)

    open fun onCreate(holder: BaseViewHolder, binding: VB) {}
    open fun onItemClick(item: T?) {}
    open fun onItemLongClick(item: T?) {}
    open fun onRecycleView(holder: BaseViewHolder) {}

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val holder = BaseViewHolder(binding)
        onCreate(holder, binding)

        holder.itemView.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition)
            onItemClick(item)
        }

        holder.itemView.setOnLongClickListener {
            val item = getItem(holder.bindingAdapterPosition)
            onItemLongClick(item)
            return@setOnLongClickListener true
        }

        return holder
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        onBind(holder, item)
    }

    final override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        onRecycleView(holder)
    }

    inner class BaseViewHolder(val binding: VB): RecyclerView.ViewHolder(binding.root)
}

