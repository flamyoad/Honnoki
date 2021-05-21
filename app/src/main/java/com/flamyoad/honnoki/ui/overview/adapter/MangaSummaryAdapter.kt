package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.FragmentMangaSummaryBinding
import com.flamyoad.honnoki.databinding.MangaSummaryItemBinding
import com.flamyoad.honnoki.model.MangaOverview
import com.flamyoad.honnoki.model.State
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kennyc.view.MultiStateView
import java.lang.IllegalArgumentException

class MangaSummaryAdapter: RecyclerView.Adapter<MangaSummaryAdapter.SummaryViewHolder>() {

    private var mangaOverviewState: State<MangaOverview> = State.Loading

    private val genreListAdapter = GenreListAdapter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val binding = MangaSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = SummaryViewHolder(binding)

        holder.setupGenreRecyclerView()

        return holder
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.bind(mangaOverviewState)
    }

    override fun getItemCount(): Int = 1

    fun setItem(mangaOverviewState: State<MangaOverview>) {
        this.mangaOverviewState = mangaOverviewState
        notifyDataSetChanged()
    }

    inner class SummaryViewHolder(val binding: MangaSummaryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun setupGenreRecyclerView() {
            val context = binding.root.context
            val flexLayoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }

            with(binding.listGenres) {
                adapter = genreListAdapter
                layoutManager = flexLayoutManager
                isNestedScrollingEnabled = false
            }
        }

        fun bind(mangaOverviewState: State<MangaOverview>) {
            when (mangaOverviewState) {
                is State.Success -> { showMangaOverview(mangaOverviewState.value) }
                is State.Error -> { binding.multiStateView.viewState = MultiStateView.ViewState.ERROR }
                is State.Loading -> { binding.multiStateView.viewState = MultiStateView.ViewState.LOADING }
            }
        }

        private fun showMangaOverview(overview: MangaOverview) {
            genreListAdapter.setList(overview.genres)

            with(binding) {
                multiStateView.viewState = MultiStateView.ViewState.CONTENT
                expandableTextView.text = overview.summary
            }
        }
    }
}