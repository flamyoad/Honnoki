package com.flamyoad.honnoki.ui.overview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.LanguageFilterContentItemBinding

import com.flamyoad.honnoki.ui.overview.model.LanguageFilter

class LanguageFilterContentAdapter(
    context: Context,
    private val onLanguageClick: (LanguageFilter) -> Unit,
    private val inflater: LayoutInflater = LayoutInflater.from(context)
) : ArrayAdapter<LanguageFilter>(context, R.layout.language_filter_content_item) {

    var items: List<LanguageFilter> = emptyList()
        get() {
            return field
        }
        set(value) {
            field = value
            clear()
            addAll(value)
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: LanguageViewHolder?

        val view = if (convertView == null) {
            val binding = LanguageFilterContentItemBinding.inflate(inflater, parent, false)
            holder = LanguageViewHolder(binding)
            binding.root.tag = holder
            holder.bind(getItem(position))
            binding.root
        } else {
            holder = convertView.tag as LanguageViewHolder
            holder.bind(getItem(position))
            convertView
        }
        view.setOnClickListener {
            val language = items.getOrNull(position) ?: return@setOnClickListener
            onLanguageClick.invoke(language)
        }

        return view
    }

    override fun getItem(position: Int): LanguageFilter {
        return items[position]
    }

    inner class LanguageViewHolder(val binding: LanguageFilterContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lang: LanguageFilter) {
            binding.txtLanguage.text = lang.locale
        }
    }
}

