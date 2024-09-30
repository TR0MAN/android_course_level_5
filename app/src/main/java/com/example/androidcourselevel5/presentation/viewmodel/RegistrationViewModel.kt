package com.example.androidcourselevel5.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcourselevel5.data.retrofit.model.CreateUserModel
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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val helper: ActivityHelper,
    private val storage: DataStorage,
    private val userAPI: ServerUsersActions
): ViewModel() {

    private val _registrationResultSuccess = MutableLiveData<UserData>()
    val registrationResultSuccess: LiveData<UserData> = _registrationResultSuccess

    private val _registrationResultError = MutableLiveData<Response<ServerResponse>>()
    val registrationResultError: LiveData<Response<ServerResponse>> = _registrationResultError

    private val _registrationResultException = MutableLiveData<Boolean>(false)
    val registrationResultException: LiveData<Boolean> = _registrationResultException

    private val _registrationResultTimeout = MutableLiveData<Boolean>(false)
    val registrationResultTimeout: LiveData<Boolean> = _registrationResultTimeout

    private val _requestProgressBar = MutableLiveData<Boolean>(false)
    val requestProgressBar: LiveData<Boolean> = _requestProgressBar

    private lateinit var newUserDataForRegistration: CreateUserModel

    fun registerNewUser(newUserData: CreateUserModel) {
        viewModelScope.launch {
            _requestProgressBar.postValue(true)
            val job = withTimeoutOrNull(RequestConst.REQUEST_MAX_DELAY) {
                try {
                    val requestResult = userAPI.registerNewUser(newUserData)
                    delay(RequestConst.REQUEST_DELAY_TIME)
                    checkingServerResponse(requestResult)

                } catch(e: ConnectException) {
                    _registrationResultException.postValue(true)
                }
            }
            if (job == null) {
                _registrationResultTimeout.postValue(true)
            }
            _requestProgressBar.postValue(false)
        }
    }

    private fun checkingServerResponse(response: Response<ServerResponse>) {
        if (response.isSuccessful) {
            response.body()?.data?.let { _registrationResultSuccess.postValue(it) }
        } else {
            _registrationResultError.postValue(response)
        }
    }

    fun saveAllUserDataToDataStorage(userData: UserData, password: String, checkboxIsChecked: Boolean) {
        storage.saveAccessToken(userData.accessToken)
        storage.saveRefreshToken(userData.refreshToken)
        storage.saveBigUserData(userData.user.name, userData.user.career, userData.user.address)
        storage.saveUserId(userData.user.id)
        if (checkboxIsChecked) {
            storage.saveCheckboxState(checked = checkboxIsChecked)
            storage.saveEmail(userData.user.email)
            storage.savePassword(password)
        }
    }

    fun getBirthday(birthdayDate: String): Date? {
        return helper.getDateFromBirthdayString(birthday = birthdayDate)
    }

    fun saveRegistrationUserData(registrationUserData: CreateUserModel) {
        newUserDataForRegistration = registrationUserData
    }

    fun getRegistrationUserData(): CreateUserModel {
        return newUserDataForRegistration
    }
}
