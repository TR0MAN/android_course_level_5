package com.example.androidcourselevel5.presentation.viewmodel


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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val storage: DataStorage,
    private val userAPI: ServerUsersActions
): ViewModel() {

    private val _settingsResultSuccess = MutableLiveData<UserData>()
    val settingsResultSuccess: LiveData<UserData> = _settingsResultSuccess

    private val _settingsResultError = MutableLiveData<Response<ServerResponse>>()
    val settingsResultError: LiveData<Response<ServerResponse>> = _settingsResultError

    private val _settingsResultException = MutableLiveData<Boolean>(false)
    val settingsResultException: LiveData<Boolean> = _settingsResultException

    private val _settingsResultTimeout = MutableLiveData<Boolean>(false)
    val settingsResultTimeout: LiveData<Boolean> = _settingsResultTimeout

    private val _requestProgressBar = MutableLiveData<Boolean>(false)
    val requestProgressBar: LiveData<Boolean> = _requestProgressBar

    fun refreshUserData() {
        viewModelScope.launch {
            _requestProgressBar.postValue(true)
            val job = withTimeoutOrNull(RequestConst.REQUEST_MAX_DELAY) {
                try {
                    val requestResult = userAPI.authoriseUser(
                        UserAuthorisationEntity(
                            email = storage.getEmail(),
                            password = storage.getPassword())
                    )
                    delay(RequestConst.REQUEST_DELAY_TIME)
                    checkingServerResponse(requestResult)
                } catch(e: ConnectException) {
                    _settingsResultException.postValue(true)
                }
            }
            if (job == null) {
                _settingsResultTimeout.postValue(true)
            }
            _requestProgressBar.postValue(false)
        }
    }

    private fun checkingServerResponse(response: Response<ServerResponse>) {
        if (response.isSuccessful) {
            response.body()?.data?.let { _settingsResultSuccess.postValue(it) }
        } else {
            _settingsResultError.postValue(response)
        }
    }

    fun getDataForSetToUI() = storage.getUserDataForUI()

    fun getCheckboxState() = storage.getCheckboxState()

    fun clearStorageData() { storage.clearStorageData() }

    fun refreshTokens(accessToken: String, refreshToken: String) {
        storage.saveAccessToken(accessToken = accessToken)
        storage.saveRefreshToken(refreshToken = refreshToken)
    }


}