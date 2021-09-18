package com.flamyoad.honnoki.ui.download.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.common.adapter.BaseAdapter
import com.flamyoad.honnoki.databinding.DownloadPickerActionItemsBinding

class DownloadActionItemsAdapter(
    private val sortList: () -> Unit,
    private val changeListType: () -> Unit,
    private val selectAll: () -> Unit,
    private val unselectAll: () -> Unit,
) : BaseAdapter<Int, DownloadPickerActionItemsBinding>() {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DownloadPickerActionItemsBinding
        get() = DownloadPickerActionItemsBinding::inflate

    override fun onCreate(holder: BaseViewHolder, binding: DownloadPickerActionItemsBinding) {
        super.onCreate(holder, binding)
        with(binding) {
            btnSort.setOnClickListener { sortList.invoke() }
            btnListType.setOnClickListener { changeListType.invoke() }
            btnSelectAll.setOnClickListener { selectAll.invoke() }
            btnUnselectAll.setOnClickListener { unselectAll.invoke() }
        }
    }

    override fun onBind(holder: BaseViewHolder, item: Int) {
        val resources = holder.context.resources
        holder.binding.txtTotalChapters.text = if (item == 0) {
            resources.getString(R.string.chapter_list_no_items)
        } else {
            resources.getQuantityString(
                R.plurals.chapter_list_total,
                item,
                item
            )
        }
    }

    fun setItem(totalChapters: Int) {
        setList(listOf(totalChapters))
        notifyDataSetChanged()
    }

    override val itemsCanBeClicked: Boolean
        get() = false

    override fun getItemCount(): Int = 1
}