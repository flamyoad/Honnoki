package com.flamyoad.honnoki.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class DeleteBookmarkGroupDialog : DialogFragment() {

    private val viewModel: DeleteBookmarkGroupDialogViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())

        val groupId = arguments?.getLong(BOOKMARK_GROUP_ID)
        val groupName = arguments?.getString(BOOKMARK_GROUP_NAME)

        builder.apply {
            setTitle(groupName)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val groupId = arguments?.getLong(BOOKMARK_GROUP_ID)
        viewModel.setBookmarkGroupId(requireNotNull(groupId))

    }

    companion object {
        const val TAG = "DELETE_BOOKMARK_GROUP_DIALOG"

        const val BOOKMARK_GROUP_ID = "BOOKMARK_GROUP_ID"
        const val BOOKMARK_GROUP_NAME = "BOOKMARK_GROUP_NAME"

        fun newInstance(bookmarkGroupId: Long, bookmarkGroupName: String) =
            DeleteBookmarkGroupDialog().apply {
                arguments = Bundle().apply {
                    putLong(BOOKMARK_GROUP_ID, bookmarkGroupId)
                    putString(BOOKMARK_GROUP_NAME, bookmarkGroupName)
                }
            }
    }
}