package com.flamyoad.honnoki.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.honnoki.api.DM5Api
import com.flamyoad.honnoki.api.MangakalotApi
import com.flamyoad.honnoki.api.ReadMangaApi
import com.flamyoad.honnoki.api.SenMangaApi
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.DynamicGenre
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.databinding.MangaGenreDialogPickerBinding
import com.flamyoad.honnoki.ui.lookup.MangaLookupActivity
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.parameter.parametersOf
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class GenrePickerDialog : DialogFragment() {

    private var _binding: MangaGenreDialogPickerBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val sourceName by lazy { arguments?.getString(SOURCE) }

    private val viewModel: GenrePickerViewModel by viewModel {
        parametersOf(sourceName)
    }

    private val source: Source by lazy {
        Source.valueOf(requireNotNull(sourceName))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding =
            MangaGenreDialogPickerBinding.inflate(layoutInflater, null, false)

        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
        }

        val dialog = builder.create()
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val genreAdapter = MangaGenreAdapter(this::openLookupActivity)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)

        with(binding.listGenre) {
            adapter = genreAdapter
            layoutManager = gridLayoutManager
        }

        lifecycleScope.launchWhenResumed {
            viewModel.dynamicGenres().collectLatest {
                when (it) {
                    is State.Success -> {
                        binding.multiStateView.viewState =
                            MultiStateView.ViewState.CONTENT
                        genreAdapter.submitList(it.value)
                    }
                    is State.Error -> {
                        binding.multiStateView.viewState =
                            MultiStateView.ViewState.ERROR
                    }
                    is State.Loading -> {
                        binding.multiStateView.viewState =
                            MultiStateView.ViewState.LOADING
                    }
                }
            }
        }
    }

    private fun openLookupActivity(genre: DynamicGenre) {
        val params = when (source) {
            Source.DM5 -> DM5Api.getDm5GenreUrl(genre.constantValue ?: return)
            Source.MANGAKALOT -> MangakalotApi.getMangakalotGenreUrl(
                genre.constantValue ?: return
            )
            Source.SENMANGA -> SenMangaApi.getSenmangaGenreUrl(
                genre.constantValue ?: return
            )
            Source.READMANGA -> ReadMangaApi.getReadMngGenreUrl(
                genre.constantValue ?: return
            )
            Source.MANGADEX -> genre.id
            else -> throw NotImplementedError("")
        }

        MangaLookupActivity.startActivity(
            context = requireContext(),
            params = params,
            name = genre.name,
            source = source,
            lookupType = LookupType.GENRE
        )

        dialog?.dismiss()
    }

    companion object {
        const val TAG = "genre_picker_dialog"
        const val SOURCE = "source"

        fun newInstance(source: Source) = GenrePickerDialog().apply {
            arguments = Bundle().apply {
                putString(SOURCE, source.toString())
            }
        }
    }
}