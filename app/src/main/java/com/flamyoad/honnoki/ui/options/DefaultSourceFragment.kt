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
import com.flamyoad.honnoki.databinding.FragmentDefaultSourceBinding
import com.flamyoad.honnoki.ui.options.adapter.SourceOptionsAdapter
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DefaultSourceFragment : Fragment() {

    private var _binding: FragmentDefaultSourceBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: OptionsViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefaultSourceBinding.inflate(inflater, container, false)
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
        val sourceAdapter = SourceOptionsAdapter(viewModel::setHomePreferredSource)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        with (binding.listSources) {
            adapter = sourceAdapter
            layoutManager = linearLayoutManager
        }

        lifecycleScope.launchWhenResumed {
            viewModel.sourceOptionList.collectLatest {
                sourceAdapter.setList(it)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DefaultSourceFragment()
    }
}