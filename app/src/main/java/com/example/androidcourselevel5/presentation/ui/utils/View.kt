package com.example.androidcourselevel5.presentation.ui.utils

import android.view.View
import android.widget.TextView
import com.example.androidcourselevel5.R

fun TextView.clear() {
    text = resources.getText(R.string.empty_field_text)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visibleIf(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun String.convertToToken(): String = "Bearer $this"