package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T: Any, VB: ViewBinding>
    (comparator: DiffUtil.ItemCallback<T>): ListAdapter<T, BaseListAdapter<T, VB>.BaseViewHolder>(comparator) {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    abstract fun onCreate(holder: BaseViewHolder, binding: VB)
    abstract fun onBind(holder: BaseViewHolder, item: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val holder = BaseViewHolder(binding)
        onCreate(holder, binding)
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = getItem(position)
        onBind(holder, item)
    }

    inner class BaseViewHolder(val binding: VB): RecyclerView.ViewHolder(binding.root)
}

