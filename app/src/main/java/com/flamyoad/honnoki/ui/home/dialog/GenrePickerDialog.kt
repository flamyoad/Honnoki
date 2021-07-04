package com.flamyoad.honnoki.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.honnoki.api.DM5Api
import com.flamyoad.honnoki.api.MangakalotApi
import com.flamyoad.honnoki.api.ReadMangaApi
import com.flamyoad.honnoki.api.SenMangaApi
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.databinding.MangaGenreDialogPickerBinding
import com.flamyoad.honnoki.ui.lookup.MangaLookupActivity
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.flamyoad.honnoki.utils.extensions.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class GenrePickerDialog : DialogFragment() {

    private var _binding: MangaGenreDialogPickerBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: GenrePickerViewModel by viewModel()

    private val source: Source by lazy {
        Source.valueOf(requireNotNull(arguments?.getString(SOURCE)))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = MangaGenreDialogPickerBinding.inflate(layoutInflater, null, false)

        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Find Manga by Genre")
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

        genreAdapter.submitList(viewModel.genreList)
    }

    private fun openLookupActivity(genre: GenreConstants) {
        val params = when (source) {
            Source.DM5 -> DM5Api.getDm5GenreUrl(genre)
            Source.MANGAKALOT -> MangakalotApi.getMangakalotGenreUrl(genre)
            Source.SENMANGA -> SenMangaApi.getSenmangaGenreUrl(genre)
            Source.READMANGA -> ReadMangaApi.getReadMngGenreUrl(genre)
            else -> throw NotImplementedError("")
        }

        MangaLookupActivity.startActivity(
            context = requireContext(),
            params = params,
            name = genre.toReadableName(requireContext()),
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