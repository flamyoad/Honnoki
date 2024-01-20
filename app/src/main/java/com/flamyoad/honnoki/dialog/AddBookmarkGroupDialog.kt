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
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.DialogAddBookmarkGroupBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddBookmarkGroupDialog : DialogFragment() {
    private val viewModel: AddBookmarkGroupDialogViewModel by viewModel()

    private var _binding: DialogAddBookmarkGroupBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddBookmarkGroupBinding.inflate(layoutInflater, null, false)

        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Create new group")
            setView(binding.root)
            setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                viewModel.createNewGroup()
            }
            setNegativeButton("Return") { dialogInterface: DialogInterface, i: Int ->

            }
        }

        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fieldName.addTextChangedListener(object : TextWatcher {
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
        const val TAG = "ADD_BOOKMARK_GROUP"
        fun newInstance() = AddBookmarkGroupDialog()
    }

}