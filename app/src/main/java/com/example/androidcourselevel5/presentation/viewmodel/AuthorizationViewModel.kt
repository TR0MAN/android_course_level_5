package com.example.androidcourselevel5.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcourselevel5.data.retrofit.model.ServerResponse
import com.example.androidcourselevel5.data.retrofit.model.UserAuthorisationEntity
import com.example.androidcourselevel5.data.retrofit.model.UserData
import com.example.androidcourselevel5.domain.constants.RequestConst
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.domain.repository.ServerUsersActions
import com.example.androidcourselevel5.presentation.ui.ActivityHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val helper: ActivityHelper,
    private val storage: DataStorage,
    private val userAPI: ServerUsersActions
): ViewModel() {

    private val _authorisationResultSuccess = MutableLiveData<UserData>()
    val authorisationResultSuccess: LiveData<UserData> = _authorisationResultSuccess

    private val _authorisationResultError = MutableLiveData<Response<ServerResponse>>()
    val authorisationResultError: LiveData<Response<ServerResponse>> = _authorisationResultError

    private val _authorisationResultException = MutableLiveData<Boolean>(false)
    val authorisationResultException: LiveData<Boolean> = _authorisationResultException

    private val _authorisationResultTimeout = MutableLiveData<Boolean>(false)
    val authorisationResultTimeout: LiveData<Boolean> = _authorisationResultTimeout

    private val _requestProgressBar = MutableLiveData<Boolean>(false)
    val requestProgressBar: LiveData<Boolean> = _requestProgressBar

    private val isPressedSignInButton = MutableLiveData<Boolean>(false)
    private val isPressedRegistrationButton = MutableLiveData<Boolean>(false)

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

    fun saveAllUserDataToDataStorage(userData: UserData, password: String, checkboxIsChecked: Boolean) {
        storage.saveAccessToken(userData.accessToken)
        storage.saveRefreshToken(userData.refreshToken)
        storage.saveBigUserData(userData.user.name, userData.user.career, userData.user.address)
        storage.saveUserId(userData.user.id)
        if (checkboxIsChecked) {
            saveCheckboxStatus(isChecked = checkboxIsChecked)
            saveEmailAndPassword(email = userData.user.email, password = password)
        }
    }

    fun pressSignInButton() {
        isPressedSignInButton.value = !isPressedSignInButton.value!!
    }

    fun pressRegistrationButton() {
        isPressedRegistrationButton.value = !isPressedRegistrationButton.value!!
    }

    fun getSignInButtonState() = isPressedSignInButton.value!!
    fun getRegistrationButtonState() = isPressedRegistrationButton.value!!

    fun authoriseUser(email: String, password: String){
        viewModelScope.launch {
            _requestProgressBar.postValue(true)
            val job = withTimeoutOrNull(RequestConst.REQUEST_MAX_DELAY) {
                try {
                    val requestResult = userAPI.authoriseUser(
                        UserAuthorisationEntity(email = email, password = password) )
                    delay(RequestConst.REQUEST_DELAY_TIME)
                    checkingServerResponse(requestResult)
                } catch(e: ConnectException) {
                    _authorisationResultException.postValue(true)
                }
            }
            if (job == null) {
                _authorisationResultTimeout.postValue(true)
            }
            _requestProgressBar.postValue(false)
        }
    }

    private fun checkingServerResponse(response: Response<ServerResponse>) {
        if (response.isSuccessful) {
            response.body()?.data?.let { _authorisationResultSuccess.postValue(it) }
        } else {
            _authorisationResultError.postValue(response)
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