package com.flamyoad.honnoki.ui.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.databinding.MangaSummaryItemBinding
import com.flamyoad.honnoki.model.Genre
import com.flamyoad.honnoki.model.MangaOverview
import com.flamyoad.honnoki.model.State
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kennyc.view.MultiStateView

class MangaSummaryAdapter: RecyclerView.Adapter<MangaSummaryAdapter.SummaryViewHolder>() {

    private var mangaOverviewState: State<MangaOverview> = State.Loading

    private var genreListState: State<List<Genre>> = State.Loading

    private val genreListAdapter = GenreListAdapter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val binding = MangaSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = SummaryViewHolder(binding)

        holder.setupGenreRecyclerView()

        return holder
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1

    fun setMangaOverview(mangaOverviewState: State<MangaOverview>) {
        this.mangaOverviewState = mangaOverviewState
        notifyDataSetChanged()
    }

    fun setGenres(genreListState: State<List<Genre>>) {
        this.genreListState = genreListState
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

        fun bind() {
            when (val overview = mangaOverviewState) {
                is State.Success -> { binding.expandableTextView.text = overview.value.summary }
                is State.Error -> { binding.multiStateView.viewState = MultiStateView.ViewState.ERROR }
                is State.Loading -> { binding.multiStateView.viewState = MultiStateView.ViewState.LOADING }
            }

            when (val genreState = genreListState) {
                is State.Success -> { genreListAdapter.setList(genreState.value) }
                is State.Error -> { binding.multiStateView.viewState = MultiStateView.ViewState.ERROR }
                is State.Loading -> { binding.multiStateView.viewState = MultiStateView.ViewState.LOADING }
            }

            val hasCompletedLoading = mangaOverviewState is State.Success && genreListState is State.Success
            if (hasCompletedLoading) {
                binding.multiStateView.viewState = MultiStateView.ViewState.CONTENT
            }
        }
    }
}