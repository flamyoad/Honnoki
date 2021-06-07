package com.flamyoad.honnoki.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

class DeleteBookmarkGroupDialog: DialogFragment() {

    private val viewModel: DeleteBookmarkGroupDialogViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val groupId = arguments?.getLong(BOOKMARK_GROUP_ID)

        builder.apply {
            setMessage("Are you sure you want to delete this bookmark group? Existing items will be lost")
            setPositiveButton("Delete") { dialogInterface, i ->
                viewModel.deleteGroup(requireNotNull(groupId))
            }
            setNegativeButton("Return") { dialogInterface, i ->

            }
        }

        val dialog = builder.create()
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val groupId = arguments?.getLong(BOOKMARK_GROUP_ID)
        viewModel.setBookmarkGroupId(requireNotNull(groupId))

        super.onActivityCreated(savedInstanceState)
        viewModel.bookmarkGroup.observe(this) {
            dialog?.setTitle(it?.name)
        }
    }

    companion object {
        const val TAG = "DELETE_BOOKMARK_GROUP_DIALOG"

        const val BOOKMARK_GROUP_ID = "BOOKMARK_GROUP_ID"

        fun newInstance(bookmarkGroupId: Long) = DeleteBookmarkGroupDialog().apply {
            arguments = Bundle().apply {
                putLong(BOOKMARK_GROUP_ID, bookmarkGroupId)
            }
        }
    }
}