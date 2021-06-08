package com.flamyoad.honnoki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T: Any, VB: ViewBinding>
    : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    private var itemList: List<T> = emptyList()

    /**
     * This field must be overridden to provide layout inflater for the base class
     */
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    /**
     * Callback method for onBindViewHolder()
     */
    abstract fun onBind(holder: BaseViewHolder, item: T)

    /**
     * Callback method for onCreateViewHolder()
     */
    open fun onCreate(holder: BaseViewHolder, binding: VB) {}

    /**
     * Callback method for onClick() on the root layout of item
     */
    open fun onItemClick(item: T) {}

    /**
     * Callback method for onViewRecycled()
     */
    open fun onRecycleView(holder: BaseViewHolder) {}

    /**
     * Set this to false when you are using dummy item as a child for ConcatAdapter.
     * This prevents IndexOutOfException from being thrown when the layout is being clicked
     */
    open val itemLayoutsAreClickable: Boolean = true

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val holder = BaseViewHolder(binding)
        onCreate(holder, binding)

        if (itemLayoutsAreClickable) {
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

    inner class BaseViewHolder(val binding: VB): RecyclerView.ViewHolder(binding.root)
}