package com.example.androidcourselevel5.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.presentation.ui.ActivityHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val helper: ActivityHelper,
    private val storage: DataStorage
): ViewModel() {

    fun getBirthday(birthdayDate: String): Date? {
        return helper.getDateFromBirthdayString(birthday = birthdayDate)
    }

    // add accessToken, RefreshToken and maybe userID
    fun saveAllUserData(email: String, password: String, checkbox: Boolean, userName: String,
                        career: String, address: String) {
        with(storage) {
            saveEmail(email = email)
            savePassword(password = password)
            saveCheckboxState(checked = checkbox)
            saveBigUserData(name = userName, career = career, address = address)
        }
    }

}