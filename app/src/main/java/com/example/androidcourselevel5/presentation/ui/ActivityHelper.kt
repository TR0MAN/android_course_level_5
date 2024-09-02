package com.example.androidcourselevel5.presentation.ui

import android.content.Context
import android.util.Patterns
import com.example.androidcourselevel5.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


class ActivityHelper @Inject constructor(private val context: Context) {

    fun checkingEmailFiled(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun checkingPasswordField(password: String): String {
        if (password.length < 8) {
            return context.getString(R.string.error_min_8_symbols_password)
        }
        if (!password.matches(".*[A-Z].*".toRegex())) {
            return context.getString(R.string.error_upper_case_symbol)
        }
        if (!password.matches(".*[a-z].*".toRegex())) {
            return context.getString(R.string.error_lower_case_symbol)
        }
        if (!password.matches(".*[0-9].*".toRegex())) {
            return context.getString(R.string.error_contain_number)
        }
        if (!password.matches(".*[!@#\$%^&*].*".toRegex())) {
            return context.getString(R.string.error_contain_special_symbol)
        }
        if (password.matches(".*[~`()_+|\\?/.{}\\[\\],<>=\\-].*".toRegex())) {
            return context.getString(R.string.error_include_wrong_spec_symbols)
        }
        if (password.matches(".*[ ].*".toRegex())) {
            return context.getString(R.string.error_include_white_space)
        }
        return context.getString(R.string.validate_success)
    }

    fun getDateFromBirthdayString(birthday: String): Date? {
        val dateList = birthday.split('/')
        val calendar = Calendar.getInstance(). apply {
            set(dateList[2].toInt(), dateList[1].toInt().minus(1), dateList[0].toInt(), 0,0, 0)
        }
        return calendar.time
    }

}