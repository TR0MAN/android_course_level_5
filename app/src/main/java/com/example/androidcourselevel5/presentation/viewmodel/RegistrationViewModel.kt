package com.example.androidcourselevel5.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidcourselevel5.presentation.ui.ActivityHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val helper: ActivityHelper
): ViewModel() {

    fun getBirthday(birthdayDate: String): Date? {
        return helper.getDateFromBirthdayString(birthday = birthdayDate)
    }

}