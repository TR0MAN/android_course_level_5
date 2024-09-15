package com.example.androidcourselevel5.domain.repository

import com.example.androidcourselevel5.data.retrofit.model.CreateUserModel
import com.example.androidcourselevel5.data.retrofit.model.ExtensionServerResponse
import com.example.androidcourselevel5.data.retrofit.model.ServerResponse
import com.example.androidcourselevel5.data.retrofit.model.UserAuthorisationEntity
import retrofit2.Response

interface ServerUsersActions {

    suspend fun registerNewUser(newUserData: CreateUserModel): Response<ServerResponse>

    suspend fun authoriseUser(userLoginData: UserAuthorisationEntity): Response<ServerResponse>

    suspend fun getAllUsers(token: String): Response<ExtensionServerResponse>

}