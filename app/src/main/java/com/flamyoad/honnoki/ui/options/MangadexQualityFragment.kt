package com.flamyoad.honnoki.ui.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentMangadexQualityBinding
import com.flamyoad.honnoki.ui.options.adapter.MangadexQualityModeAdapter
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MangadexQualityFragment : Fragment() {

    private var _binding: FragmentMangadexQualityBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: OptionsViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMangadexQualityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding.toolbarLayout.toolbar) {
            setupWithNavController(findNavController())
        }
        initList()
    }

    private fun initList() {
        val listAdapter = MangadexQualityModeAdapter(viewModel::setMangadexQuality)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        with (binding.listQuality) {
            adapter = listAdapter
            layoutManager = linearLayoutManager
        }

        lifecycleScope.launchWhenResumed {
            viewModel.mangadexQualityOptionList.collectLatest {
                listAdapter.setList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MangadexQualityFragment()
    }
}