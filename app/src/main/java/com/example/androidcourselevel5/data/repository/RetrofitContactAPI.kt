package com.example.androidcourselevel5.data.repository

import com.example.androidcourselevel5.data.retrofit.model.ContactId
import com.example.androidcourselevel5.data.retrofit.model.ContactsServerResponse
import com.example.androidcourselevel5.domain.repository.ServerContactsActions
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitContactAPI: ServerContactsActions {
    @GET("users/{userId}/contacts")
    override suspend fun getUserContacts(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String
    ): Response<ContactsServerResponse>

    @Headers("Content-type: application/json")
    @PUT("users/{userId}/contacts")
    override suspend fun addContactToUserContactList(
        @Path("userId") userId: Int,
        @Header("Authorization") accessToken: String,
        @Body contactIdForAdding: ContactId
    ): Response<ContactsServerResponse>

    @DELETE("users/{userId}/contacts/{contactId}")
    override suspend fun deleteContactFromUserList(
        @Path("userId") userId: Int,
        @Path("contactId") contactIdForDelete: Int,
        @Header("Authorization") accessToken: String
    ): Response<ContactsServerResponse>


}