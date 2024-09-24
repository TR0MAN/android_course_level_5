package com.example.androidcourselevel5.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcourselevel5.data.retrofit.model.Contact
import com.example.androidcourselevel5.data.retrofit.model.ContactId
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
import javax.inject.Singleton

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val storage: DataStorage,
    private val contactAPI: ServerContactsActions
): ViewModel() {


    // ViewPager Fragment fields
    private val _tabLayoutVisibility = MutableLiveData<Boolean>(true)
    val tabLayoutVisibility: LiveData<Boolean> = _tabLayoutVisibility

    // Contact list fields
    private val _contactList = MutableLiveData<List<Contact>>()
    val contactList: LiveData<List<Contact>> = _contactList

    // Add contact fields
    private val _addContactResultError = MutableLiveData<Response<ContactsServerResponse>>()
    val addContactResultError: LiveData<Response<ContactsServerResponse>> = _addContactResultError

    private val _addContactResultException = MutableLiveData<Boolean>(false)
    val addContactResultException: LiveData<Boolean> = _addContactResultException

    private val _addContactResultTimeout = MutableLiveData<Boolean>(false)
    val addContactResultTimeout: LiveData<Boolean> = _addContactResultTimeout

    private val _requestProgressBar = MutableLiveData<Boolean>(false)
    val requestProgressBar: LiveData<Boolean> = _requestProgressBar

    private var contactIdForAdding = DEFAULT_INT_VALUE

    // Methods for ContactList fragment
    fun updateUserContactList(contactList: List<Contact>){
        _contactList.value = contactList
    }

    fun setTabLayoutVisibility(visibility: Boolean){
        _tabLayoutVisibility.value = visibility
    }

    // Methods for Add Contact fragment
    fun addToContactList() {
        viewModelScope.launch {
            _requestProgressBar.postValue(true)
            val job = withTimeoutOrNull(RequestConst.REQUEST_MAX_DELAY) {
                try {
                    val requestResult =
                        contactAPI.addContactToUserContactList(
                            userId = storage.getUserId(),
                            accessToken = storage.getAccessToken().convertToToken(),
                            contactIdForAdding = ContactId(contactIdForAdding) )
                    delay(RequestConst.REQUEST_DELAY_TIME)
                    checkingContactServerResponse(requestResult)
                } catch(e: ConnectException) {
                    _addContactResultException.postValue(true)
                }
            }
            if (job == null) {
                _addContactResultTimeout.postValue(true)
            }
            _requestProgressBar.postValue(false)
        }
    }

    private fun checkingContactServerResponse(response: Response<ContactsServerResponse>) {
        if (response.isSuccessful) {
            _contactList.value = response.body()?.data?.contacts
            resetErrorStates()
        } else {
            _addContactResultError.postValue(response)
        }
    }

    private fun resetErrorStates() {
        _addContactResultException.value = false
        _addContactResultTimeout.value = false
    }

    fun addContact(contactId: Int) {
        contactIdForAdding = contactId
        addToContactList()
    }

    companion object {
        const val DEFAULT_INT_VALUE = 0
    }

}