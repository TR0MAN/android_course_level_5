package com.example.androidcourselevel5.data.repository

import com.example.androidcourselevel5.data.retrofit.model.CreateUserModel
import com.example.androidcourselevel5.data.retrofit.model.ExtensionServerResponse
import com.example.androidcourselevel5.data.retrofit.model.ServerResponse
import com.example.androidcourselevel5.data.retrofit.model.UserAuthorisationEntity
import com.example.androidcourselevel5.domain.repository.ServerUsersActions
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitUserAPI : ServerUsersActions {

    @Headers("Content-type: application/json")
    @POST("users")
    override suspend fun registerNewUser(
        @Body registrationUserData: CreateUserModel): Response<ServerResponse>

    @Headers("Content-type: application/json")
    @POST("login")
    override suspend fun authoriseUser(
        @Body userLoginData: UserAuthorisationEntity): Response<ServerResponse>

    @Headers("Content-type: application/json")
    @GET("users")
    override suspend fun getAllUsers(
        @Header("Authorization") token: String): Response<ExtensionServerResponse>
}
