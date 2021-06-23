package com.flamyoad.honnoki.dialog

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.SourceSwitcherAdapter
import com.flamyoad.honnoki.databinding.DialogSourceSwitcherBinding
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.IllegalArgumentException

class SourceSwitcherDialog: BottomSheetDialogFragment() {
    private val binding by viewLifecycleLazy { DialogSourceSwitcherBinding.bind(requireView()) }

    interface Listener {
        fun onSourceSwitch(source: Source)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_source_switcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callerType = arguments?.getInt(CallerType.ARGS_KEY)
        val listener = when (callerType) {
            CallerType.ACTIVITY.ordinal -> requireActivity() as Listener
            CallerType.FRAGMENT.ordinal -> requireParentFragment() as Listener
            else -> throw IllegalArgumentException("Caller is not activity or fragment")
        }

        initRecyclerView(listener)
    }

    private fun initRecyclerView(listener: Listener) {
        val sourceList = Source.values()
            .filter { it.isEnabled }
            .toList()
        val sourceAdapter = SourceSwitcherAdapter(sourceList, listener::onSourceSwitch)
        val linearLayoutManager = LinearLayoutManager(requireContext())

        with(binding.listSource) {
            adapter = sourceAdapter
            layoutManager = linearLayoutManager
        }
    }

    companion object {
        fun newInstance(caller: Any): SourceSwitcherDialog {
            val callerType = when (caller) {
                is Activity -> CallerType.ACTIVITY
                is Fragment -> CallerType.FRAGMENT
                else -> throw IllegalArgumentException("Caller is not activity or fragment")
            }

            return SourceSwitcherDialog().apply {
                arguments = Bundle().apply {
                    putInt(CallerType.ARGS_KEY, callerType.ordinal)
                }
            }
        }

        const val TAG = "SourceSwitcherDialog"
    }
}