package com.example.androidcourselevel5.data.retrofit.model

data class ServerResponse (
    val status: String,
    val code: Int,
    val message: String?,
    val data: UserData?
)

data class UsersServerResponse (
    val status: String,
    val code: Int,
    val message: String?,
    val data: AllUsers?
)

data class ContactsServerResponse(
    val status: String,
    val code: Int,
    val message: String?,
    val data: AllContacts?
)