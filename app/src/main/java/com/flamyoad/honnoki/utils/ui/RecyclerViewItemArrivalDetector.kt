package com.flamyoad.honnoki.utils.ui

import androidx.recyclerview.widget.RecyclerView

/**
 * Detects whether the first batch of items submitted by ListAdapter#submitList()
 * is now visible in the RecyclerView.
 * This workaround is needed because even after invoking submitList(),
 * the items are still being processed by DiffUtil, and still not visible on the screen.
 *
 */
fun RecyclerView.Adapter<*>.onItemsArrived(callback: () -> Unit) {
    registerAdapterDataObserver(RecyclerViewItemArrivalDetector(callback))
}

class RecyclerViewItemArrivalDetector(private val onItemsArrived: () -> Unit)
    : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        println("RecyclerViewItemArrivalDetector itemCount" + itemCount)
        if (itemCount > 0) {
            onItemsArrived.invoke()
        }
    }
}