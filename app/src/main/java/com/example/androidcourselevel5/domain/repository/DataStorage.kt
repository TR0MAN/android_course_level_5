package com.example.androidcourselevel5.domain.repository

interface DataStorage {

    fun saveEmail(email: String)
    fun getEmail(): String

    fun savePassword(password: String)
    fun getPassword(): String

    fun saveCheckboxState(checked: Boolean)
    fun getCheckboxState(): Boolean

    fun saveUserId(id: Int)
    fun getUserId(): Int

    fun saveAccessToken(accessToken: String)
    fun getAccessToken(): String

    fun saveRefreshToken(refreshToken: String)
    fun getRefreshToken(): String

    fun saveBigUserData(name:String?, career:String?, address: String?)
    fun getUserDataForUI(): List<String>

    fun clearStorageData()

}