package com.example.androidcourselevel5.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.presentation.ui.ActivityHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val helper: ActivityHelper,
    private val storage: DataStorage
): ViewModel() {

    fun validateEmail(email: String): Boolean {
        return helper.checkingEmailFiled(email = email)
    }

    fun validatePassword(password: String): String {
        return helper.checkingPasswordField(password = password)
    }

    fun validateEmailAndPassword(email: String, password: String): Boolean {
        return (validateEmail(email) && validatePassword(password) == "OK")
    }

    fun saveEmailAndPassword(email: String, password: String) {
        storage.saveEmail(email)
        storage.savePassword(password)
    }

    fun saveCheckboxStatus(isChecked: Boolean){
        storage.saveCheckboxState(checked = isChecked)
    }

    fun getCheckboxState() = storage.getCheckboxState()

}