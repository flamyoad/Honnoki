package com.flamyoad.honnoki.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.flamyoad.honnoki.databinding.DialogScreenBrightnessBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScreenBrightnessDialog : DialogFragment() {
    private var _binding: DialogScreenBrightnessBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: ScreenBrightnessViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding =
            DialogScreenBrightnessBinding.inflate(layoutInflater, null, false)
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
        }
        return builder.create()
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
        initUi()
        observeUi()
    }

    private fun initUi() {
        with(binding.seekBar) {
            max = 100
            progress = viewModel.userBrightness
            setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        viewModel.setUserBrightness(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        with(binding.toggleBrightness) {
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setUseSystemBrightness(isChecked)
            }
        }
    }

    private fun observeUi() {
        lifecycleScope.launch {
            viewModel.useSystemBrightness.collectLatest {
                binding.toggleBrightness.isChecked = it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ScreenBrightnessDialog()

        fun show(fm: FragmentManager) {
            val dialog = ScreenBrightnessDialog()
            dialog.show(fm, "brightness")
        }
    }
}