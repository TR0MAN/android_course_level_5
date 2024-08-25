package com.example.androidcourselevel5.presentation.ui.utils

import android.widget.TextView
import com.example.androidcourselevel5.R

fun TextView.clear() {
    text = resources.getText(R.string.empty_field_text)
}