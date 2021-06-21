package com.flamyoad.honnoki.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.adapter.BookmarkDialogAdapter
import com.flamyoad.honnoki.databinding.DialogAddBookmarkGroupBinding
import com.flamyoad.honnoki.databinding.DialogMoveBookmarksBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoveBookmarkDialog: DialogFragment() {
    private val viewModel: MoveBookmarkDialogViewModel by viewModel()

    private var _binding: DialogMoveBookmarksBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val groupAdapter by lazy {
        BookmarkDialogAdapter(viewModel::tickBookmarkGroup)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMoveBookmarksBinding.inflate(layoutInflater, null, false)

        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Move bookmarks to")
            setView(binding.root)
        }

        val dialog = builder.create()

        binding.btnSave.setOnClickListener {
            moveBookmark()
            setFragmentResult(REQUEST_KEY, bundleOf(MOVED_SUCCESSFULLY to true))
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

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
        setRecyclerviewSize()

        val linearLayoutManager = LinearLayoutManager(requireContext())
        with(binding.listGroups) {
            adapter = groupAdapter
            layoutManager = linearLayoutManager
            itemAnimator = null
        }

        viewModel.bookmarkGroups.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }
    }

    private fun moveBookmark() {
        val bookmarkIds = arguments?.getLongArray(BOOKMARK_IDS) ?: return
        viewModel.moveBookmarks(bookmarkIds.toList())
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
        const val TAG = "move_bookmark_group_dialog"
        const val REQUEST_KEY = "move_bookmark_request_key"
        const val MOVED_SUCCESSFULLY = "moved_successfully"
        const val BOOKMARK_IDS = "bookmark_ids"

        fun newInstance(bookmarkIdList: List<Long>) = MoveBookmarkDialog().apply {
            arguments = Bundle().apply {
                putLongArray(BOOKMARK_IDS, bookmarkIdList.toLongArray())
            }
        }
    }
}