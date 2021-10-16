package com.carkzis.android.silenus.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

/**
 * Extension functions.
 */

fun View.showSnackbar(message: String, length: Int) {
    Snackbar.make(this, message, length).run {
        show()
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).run {
        show()
    }
}