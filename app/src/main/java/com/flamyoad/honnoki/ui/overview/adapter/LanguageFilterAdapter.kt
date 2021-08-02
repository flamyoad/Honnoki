package com.flamyoad.honnoki.ui.overview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.ChapterListLanguageFilterBinding
import com.flamyoad.honnoki.ui.overview.model.LanguageFilter

class LanguageFilterAdapter(
    private val context: Context,
    private val onLanguageClick: (LanguageFilter) -> Unit) :
    RecyclerView.Adapter<LanguageFilterAdapter.ViewHolder>() {

    var items: List<LanguageFilter> = emptyList()
        get() {
            return field
        }
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val contentAdapter = LanguageFilterContentAdapter(context, onLanguageClick)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChapterListLanguageFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.languageDropdownMenu.setOnClickListener {
            binding.languageDropdownList.showDropDown()
        }

        val holder = ViewHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items)
    }

    override fun getItemCount(): Int = 1

    inner class ViewHolder(val binding: ChapterListLanguageFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(items: List<LanguageFilter>) {
            binding.languageDropdownList.setAdapter(contentAdapter)
            contentAdapter.items = items

            val selectedItem = items.firstOrNull { it.isSelected }
            selectedItem?.let {
                // https://stackoverflow.com/questions/28184543/android-autocompletetextview-not-showing-after-settext-is-called
                binding.languageDropdownList.setText(it.locale, false)
            }
        }
    }
}