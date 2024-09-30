package com.example.androidcourselevel5.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.data.retrofit.model.UsersServerResponse
import com.example.androidcourselevel5.domain.constants.RequestConst
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.domain.repository.ServerUsersActions
import com.example.androidcourselevel5.presentation.ui.utils.convertToToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject


@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val storage: DataStorage,
    private val userAPI: ServerUsersActions,
): ViewModel() {

    private val _getUsersListResultSuccess = MutableLiveData<List<Contact>>()
    val getUsersListResultSuccess: LiveData<List<Contact>> = _getUsersListResultSuccess

    private val _getUsersListResultError = MutableLiveData<Response<UsersServerResponse>>()
    val getUsersListResultError: LiveData<Response<UsersServerResponse>> = _getUsersListResultError

    private val _getUsersListResultException = MutableLiveData<Boolean>(false)
    val getUsersListResultException: LiveData<Boolean> = _getUsersListResultException

    private val _getUsersListResultTimeout = MutableLiveData<Boolean>(false)
    val getUsersListResultTimeout: LiveData<Boolean> = _getUsersListResultTimeout

    private val _requestProgressBar = MutableLiveData<Boolean>(false)
    val requestProgressBar: LiveData<Boolean> = _requestProgressBar

    private val _isActiveSearchField = MutableLiveData<Boolean>(false)
    val isActiveSearchField: LiveData<Boolean> = _isActiveSearchField

    private var filteredList = mutableListOf<Contact>()


    fun getListWithAllUsers() {
        viewModelScope.launch {
            _requestProgressBar.postValue(true)
            val job = withTimeoutOrNull(RequestConst.REQUEST_MAX_DELAY) {
                try {
                    val requestResult =
                        userAPI.getAllUsers(storage.getAccessToken().convertToToken())
                    delay(RequestConst.REQUEST_DELAY_TIME)
                    checkingUserServerResponse(requestResult)
                } catch(e: ConnectException) {
                    _getUsersListResultException.postValue(true)
                }
            }
            if (job == null) {
                _getUsersListResultTimeout.postValue(true)
            }
            _requestProgressBar.postValue(false)
        }
    }

    private fun checkingUserServerResponse(response: Response<UsersServerResponse>) {
        if (response.isSuccessful) {
            _getUsersListResultSuccess.value = response.body()?.data?.users
        } else {
            _getUsersListResultError.postValue(response)
        }
    }

    fun setSearchFieldVisibility(visibility: Boolean) {
        _isActiveSearchField.value = visibility
    }

    fun updateUsersList(){
        _getUsersListResultSuccess.value?.let {
            _getUsersListResultSuccess.value = it
        }
    }

    fun saveFilteredList(list: List<Contact>?) {
        filteredList.clear()
        list?.let {
            filteredList.addAll(list)
        }
    }

    fun getFilteredList() = filteredList

}