package com.example.androidcourselevel5.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcourselevel5.data.retrofit.model.UserAuthorisationEntity
import com.example.androidcourselevel5.data.retrofit.model.UserData
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.domain.repository.ServerUsersActions
import com.example.androidcourselevel5.presentation.ui.ActivityHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val helper: ActivityHelper,
    private val storage: DataStorage,
    private val userAPI: ServerUsersActions
): ViewModel() {

    private val _authorisationResultMutable = MutableLiveData<UserData>()
    val authorisationResult: LiveData<UserData> = _authorisationResultMutable

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

    fun authoriseUser(email: String, password: String){
        viewModelScope.launch {
            val requestResult = userAPI.authoriseUser(UserAuthorisationEntity(
                email = email, password = password
            ))
            if (requestResult.isSuccessful) {
                _authorisationResultMutable.value = requestResult.body()?.let { it.data }
            }
        }
    }

    fun saveAllUserDataToDataStorage(userData: UserData, password: String, checkboxChecked: Boolean) {
        storage.saveAccessToken(userData.accessToken)
        storage.saveRefreshToken(userData.refreshToken)
        storage.saveBigUserData(userData.user.name, userData.user.career, userData.user.address)
        storage.saveUserId(userData.user.id)
        if (checkboxChecked) {
            saveCheckboxStatus(isChecked = checkboxChecked)
            saveEmailAndPassword(email = userData.user.email, password = password)
        }
    }


    // TODO - DELETE AFTER TESTS
    fun showData(userData: UserData) {
        Log.d("TAG", "email = ${userData.user.email}")
        Log.d("TAG", "id = ${userData.user.id}")
        Log.d("TAG", "name = ${userData.user.name}")
        Log.d("TAG", "career = ${userData.user.career}")
        Log.d("TAG", "address = ${userData.user.address}")
        Log.d("TAG", "birthday = ${userData.user.birthday}")
        Log.d("TAG", "phone = ${userData.user.phone}")
        Log.d("TAG", "email = ${userData.user.email}")
        Log.d("TAG", "accessToken = ${userData.accessToken}")
        Log.d("TAG", "refreshToken = ${userData.refreshToken}")

    }
}