package com.flamyoad.honnoki.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T: Any, VB: ViewBinding>
    : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    private var itemList: List<T> = emptyList()

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    abstract fun onBind(holder: BaseViewHolder, item: T)

    open fun onCreate(holder: BaseViewHolder, binding: VB) {}
    open fun onItemClick(item: T) {}
    open fun onRecycleView(holder: BaseViewHolder) {}

    /**
     * Set this to false when you are using dummy item as a child for ConcatAdapter.
     * This prevents IndexOutOfException from being thrown when the layout is being clicked
     */
    open val itemsCanBeClicked: Boolean = true

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val holder = BaseViewHolder(binding)
        onCreate(holder, binding)

        if (itemsCanBeClicked) {
            holder.itemView.setOnClickListener {
                val item = itemList[holder.bindingAdapterPosition]
                onItemClick(item)
            }
        }
        return holder
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = itemList.getOrNull(position) ?: return
        onBind(holder, item)
    }

    final override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        onRecycleView(holder)
    }

    override fun getItemCount(): Int = itemList.size

    fun setList(list: List<T>) {
        itemList = list
        notifyDataSetChanged()
    }

    inner class BaseViewHolder(val binding: VB): RecyclerView.ViewHolder(binding.root) {
        val context: Context get() = binding.root.context
    }
}