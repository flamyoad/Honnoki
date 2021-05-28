package com.flamyoad.honnoki.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.flamyoad.honnoki.model.BookmarkGroup

class DeleteBookmarkGroupDialog: DialogFragment() {

    private val viewModel: DeleteBookmarkGroupDialogViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

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

    companion object {
        const val TAG = "DELETE_BOOKMARK_GROUP_DIALOG"

        const val BOOKMARK_GROUP_ID = "BOOKMARK_GROUP_ID"
        const val BOOKMARK_GROUP_NAME = "BOOKMARK_GROUP_NAME"

        fun newInstance(bookmarkGroup: BookmarkGroup) = DeleteBookmarkGroupDialog().apply {
            arguments = Bundle().apply {
                putLong(BOOKMARK_GROUP_ID, requireNotNull(bookmarkGroup.id))
                putString(BOOKMARK_GROUP_NAME, bookmarkGroup.name)
            }
        }
    }
}