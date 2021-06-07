package com.flamyoad.honnoki.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.databinding.DialogChangeBookmarkGroupNameBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class ChangeBookmarkGroupNameDialog: DialogFragment() {
    private val viewModel: ChangeBookmarkGroupNameViewModel by viewModel()

    private var _binding: DialogChangeBookmarkGroupNameBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogChangeBookmarkGroupNameBinding.inflate(layoutInflater, null, false)

        val builder = AlertDialog.Builder(requireContext()).apply {
            setTitle("Create new group")
            setView(binding.root)
            setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                viewModel.changeGroupName()
            }
            .setNegativeButton("Return") { dialogInterface: DialogInterface, i: Int ->

            }
        }
        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookmarkGroupId = arguments?.getLong(BOOKMARK_GROUP_ID)
        viewModel.setBookmarkGroupId(requireNotNull(bookmarkGroupId))

        binding.fieldName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setInputName(text.toString().trim())
            }
        })

        viewModel.nameAlreadyExists.observe(viewLifecycleOwner) { alreadyExists ->
            val thisDialog = (dialog as AlertDialog)

            if (alreadyExists) {
                binding.fieldNameLayout.error = "The name is already used!"
                thisDialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = false
            } else {
                binding.fieldNameLayout.error = null
                thisDialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = true
            }
        }

        viewModel.bookmarkGroup.observe(viewLifecycleOwner) {
            dialog!!.setTitle(it?.name)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.setInputName("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CHANGE_BOOKMARK_GROUP_DIALOG"

        const val BOOKMARK_GROUP_ID = "BOOKMARK_GROUP_ID"

        fun newInstance(bookmarkGroupId: Long) = ChangeBookmarkGroupNameDialog().apply {
            arguments = Bundle().apply {
                putLong(BOOKMARK_GROUP_ID, bookmarkGroupId)
            }
        }
    }
}