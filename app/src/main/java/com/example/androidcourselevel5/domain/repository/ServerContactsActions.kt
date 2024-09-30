package com.example.androidcourselevel5.domain.repository

import com.example.androidcourselevel5.data.retrofit.model.ContactId
import com.example.androidcourselevel5.data.retrofit.model.ContactsServerResponse
import retrofit2.Response

interface ServerContactsActions {

    suspend fun getUserContacts( userId: Int, accessToken: String): Response<ContactsServerResponse>

    suspend fun addContactToUserContactList(userId: Int, accessToken: String, contactIdForAdding: ContactId
    ): Response<ContactsServerResponse>

    suspend fun deleteContactFromUserList(userId: Int, contactIdForDelete: Int, accessToken: String
    ): Response<ContactsServerResponse>

}