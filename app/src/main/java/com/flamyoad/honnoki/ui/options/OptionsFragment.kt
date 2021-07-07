package com.flamyoad.honnoki.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentMoreOptionsBinding
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OptionsFragment : Fragment() {

    private var _binding: FragmentMoreOptionsBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: OptionsViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeUi()
    }

    private fun initUi() {
        Glide.with(this)
            .load(ContextCompat.getDrawable(requireContext(), R.drawable.rinze))
            .into(binding.logo)

        binding.swNightMode.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setNightMode(isChecked)
        }

        binding.layoutPreferredSource.setOnClickListener {
            val action = OptionsFragmentDirections.actionOptionsFragmentToDefaultSourceFragment()
            findNavController().navigate(action)
        }

        binding.layoutMangadexQuality.setOnClickListener {
            val action = OptionsFragmentDirections.actionOptionsFragmentToMangadexQualityFragment()
            findNavController().navigate(action)
        }
    }

    private fun observeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.nightModeEnabled.collectLatest {
                        binding.swNightMode.isChecked = it
                        enableNightMode(it)
                    }
                }
                launch {
                    viewModel.preferredSource.collectLatest {
                        binding.txtPreferredSource.text = it.title
                    }
                }
                launch {
                    viewModel.preferredMangadexQuality.collectLatest {
                        binding.txtMangadexQuality.text = when (it) {
                            MangadexQualityMode.DATA -> "Original"
                            MangadexQualityMode.DATA_SAVER -> "Compressed"
                        }
                    }
                }
            }
        }

//        lifecycleScope.launchWhenResumed {
//            viewModel.nightModeEnabled.collectLatest {
//                binding.swNightMode.isChecked = it
//                enableNightMode(it)
//            }
//        }
//
//        lifecycleScope.launchWhenResumed {
//            viewModel.preferredSource.collectLatest {
//                binding.txtPreferredSource.text = it.title
//            }
//        }
//
//        lifecycleScope.launchWhenResumed {
//            viewModel.preferredMangadexQuality.collectLatest {
//                binding.txtMangadexQuality.text = when (it) {
//                    MangadexQualityMode.DATA -> "Original"
//                    MangadexQualityMode.DATA_SAVER -> "Compressed"
//                }
//            }
//        }
    }

    private fun enableNightMode(yes: Boolean) {
        if (yes) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = OptionsFragment()
    }
}