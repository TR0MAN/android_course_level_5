package com.example.androidcourselevel5.presentation.ui.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.androidcourselevel5.R
import java.io.File

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

fun View.goneIf(isVisible: Boolean) {
    visibility = if (isVisible) View.GONE else View.VISIBLE
}

fun String.convertToToken(): String = "Bearer $this"

fun ImageView.showWithGlide(image: File?) {
    Glide.with(context)
        .load(image)
        .circleCrop()
        .placeholder(R.drawable.default_avatar)
        .into(this)
}
