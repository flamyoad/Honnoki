package com.flamyoad.honnoki.utils.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Activity.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, validateToastLength(length))
        .show()
}

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, validateToastLength(length))
        .show()
}

fun Activity.snackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this.findViewById(android.R.id.content), message, validateSnackbarLength(length))
}

fun Fragment.snackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) {
    view?.let {
        Snackbar.make(
            it.findViewById(android.R.id.content),
            message,
            validateSnackbarLength(length)
        )
    }
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, validateSnackbarLength(length))
}

fun validateToastLength(length: Int): Int {
    return if (length == Toast.LENGTH_SHORT || length == Toast.LENGTH_LONG) {
        length
    } else {
        Toast.LENGTH_SHORT
    }
}

fun validateSnackbarLength(length: Int): Int {
    return if (length == Snackbar.LENGTH_SHORT || length == Snackbar.LENGTH_LONG || length == Snackbar.LENGTH_INDEFINITE) {
        length
    } else {
        Toast.LENGTH_SHORT
    }
}
