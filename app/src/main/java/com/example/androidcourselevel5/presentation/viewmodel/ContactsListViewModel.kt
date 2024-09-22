package com.example.androidcourselevel5.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.data.retrofit.model.ContactsServerResponse
import com.example.androidcourselevel5.domain.constants.RequestConst
import com.example.androidcourselevel5.domain.repository.DataStorage
import com.example.androidcourselevel5.domain.repository.ServerContactsActions
import com.example.androidcourselevel5.presentation.ui.utils.convertToToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class ContactsListViewModel @Inject constructor(
    private val storage: DataStorage,
    private val contactAPI: ServerContactsActions
): ViewModel() {

    private val _contactListResultSuccess = MutableLiveData<List<Contact>>()
    val contactListResultSuccess: LiveData<List<Contact>> = _contactListResultSuccess

    private val _contactListResultError = MutableLiveData<Response<ContactsServerResponse>>()
    val contactListResultError: LiveData<Response<ContactsServerResponse>> = _contactListResultError

    private val _contactListResultException = MutableLiveData<Boolean>(false)
    val contactListResultException: LiveData<Boolean> = _contactListResultException

    private val _contactListResultTimeout = MutableLiveData<Boolean>(false)
    val contactListResultTimeout: LiveData<Boolean> = _contactListResultTimeout

    private val _requestProgressBar = MutableLiveData<Boolean>(false)
    val requestProgressBar: LiveData<Boolean> = _requestProgressBar

    private val _deleteContactResultError = MutableLiveData<Response<ContactsServerResponse>>()
    val deleteContactResultError: LiveData<Response<ContactsServerResponse>> = _deleteContactResultError

    private val _deleteContactResultException = MutableLiveData<Boolean>(false)
    val deleteContactResultException: LiveData<Boolean> = _deleteContactResultException

    private val _deleteContactResultTimeout = MutableLiveData<Boolean>(false)
    val deleteContactResultTimeout: LiveData<Boolean> = _deleteContactResultTimeout

    private var contactIdForDelete = DEFAULT_INT_VALUE



    fun getUserContactsList() {
        viewModelScope.launch {
            _requestProgressBar.postValue(true)
            val job = withTimeoutOrNull(RequestConst.REQUEST_MAX_DELAY) {
                try {
                    val requestResult = contactAPI.getUserContacts(
                        userId = storage.getUserId(),
                        accessToken = storage.getAccessToken().convertToToken())
                    delay(RequestConst.REQUEST_DELAY_TIME)
                    checkingServerResponse(requestResult, GET_LIST)
                } catch(e: ConnectException) {
                    _contactListResultException.postValue(true)
                }
            }
            if (job == null) {
                _contactListResultTimeout.postValue(true)
            }
            _requestProgressBar.postValue(false)
        }
    }

    private fun checkingServerResponse(response: Response<ContactsServerResponse>, action: Int) {
        if (response.isSuccessful) {
            _contactListResultSuccess.value = response.body()?.data?.contacts
            when(action) {
                GET_LIST -> resetErrorStates()
                DELETE_CONTACT -> confirmDeletion()
            }
        } else {
            when(action){
                GET_LIST -> _contactListResultError.postValue(response)
                DELETE_CONTACT -> _deleteContactResultError.postValue(response)
            }
        }
    }

    fun deleteFromContacts() {
        viewModelScope.launch {
            _requestProgressBar.postValue(true)
            val job = withTimeoutOrNull(RequestConst.REQUEST_MAX_DELAY) {
                try {
                    val requestResult = contactAPI.deleteContactFromUserList(
                        userId = storage.getUserId(), contactIdForDelete = contactIdForDelete,
                        accessToken = storage.getAccessToken().convertToToken()
                    )
                    delay(RequestConst.REQUEST_DELAY_TIME)
                    checkingServerResponse(requestResult, DELETE_CONTACT)
                } catch(e: ConnectException) {
                    _deleteContactResultException.postValue(true)
                }
            }
            if (job == null) {
                _deleteContactResultTimeout.postValue(true)
            }
            _requestProgressBar.postValue(false)
        }
    }

    private fun resetErrorStates() {
        _contactListResultException.value = false
        _contactListResultTimeout.value = false
        _deleteContactResultException.value = false
        _deleteContactResultTimeout.value = false
    }

    fun saveContactIdForDelete(contactID: Int){
        contactIdForDelete = contactID
    }

    private fun confirmDeletion() {
        if (contactIdForDelete != DEFAULT_INT_VALUE) {
            contactIdForDelete = DEFAULT_INT_VALUE
            resetErrorStates()
        }
    }

    fun isSuccessDeletion(): Boolean {
        return contactIdForDelete == DEFAULT_INT_VALUE
    }

    fun showContactDeletingSnackbar() {
//        Log.d("TAG", "runContactDeleting [deleteContactResultException = ${_deleteContactResultException.value}]")
        _deleteContactResultException.value = true
    }

    companion object {
        const val DEFAULT_INT_VALUE = 0
        const val GET_LIST = 10
        const val DELETE_CONTACT = 20

    }
}