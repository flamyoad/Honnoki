package com.flamyoad.honnoki.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.adapter.BookmarkDialogAdapter
import com.flamyoad.honnoki.databinding.DialogBookmarkGroupBinding

class BookmarkDialog : DialogFragment() {
    private val viewModel: BookmarkDialogViewModel by activityViewModels()

    private var _binding: DialogBookmarkGroupBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Bookmark to")

        _binding = DialogBookmarkGroupBinding.inflate(layoutInflater, null, false)

        dialogBuilder.setView(binding.root)

        return dialogBuilder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val overviewId = arguments?.getLong(OVERVIEW_ID) ?: -1
            viewModel.getBookmarkGroups(overviewId)
        }

        setRecyclerviewSize()

        val groupAdapter = BookmarkDialogAdapter(viewModel::toggleBookmarkGroup)
        val linearLayoutManager = LinearLayoutManager(requireContext())

        with(binding.listGroups) {
            adapter = groupAdapter
            layoutManager = linearLayoutManager
        }

        viewModel.bookmarkGroups().observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveBookmarkGroup()
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    /**
     * Set height and width of recyclerview to fixed size
     */
    private fun setRecyclerviewSize() {
        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display? = window?.getWindowManager()?.getDefaultDisplay()
        display?.getSize(size)

        binding.listGroups.apply {
            layoutParams.height = (size.y * 0.5).toInt()
            requestLayout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val OVERVIEW_ID = "OVERVIEW_ID"

        fun newInstance(overviewId: Long) = BookmarkDialog().apply {
            arguments = Bundle().apply {
                putLong(OVERVIEW_ID, overviewId)
            }
        }
    }
}