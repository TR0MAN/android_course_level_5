package com.example.androidcourselevel5.data.repository

import android.content.Context
import com.example.androidcourselevel5.domain.repository.DataStorage
import javax.inject.Inject

class SharedPrefStorage @Inject constructor(
    private val context: Context
): DataStorage {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) }

    override fun saveEmail(email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_EMAIL, email)
        }.apply()
    }

    override fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, DEFAULT_VALUE).toString()
    }

    override fun savePassword(password: String) {
        sharedPreferences.edit().apply {
            putString(KEY_PASSWORD, password)
        }.apply()
    }

    override fun getPassword(): String {
        return sharedPreferences.getString(KEY_PASSWORD, DEFAULT_VALUE).toString()
    }

    override fun saveCheckboxState(checked: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_CHECKBOX_STATE, checked)
        }.apply()
    }

    override fun getCheckboxState(): Boolean {
        return sharedPreferences.getBoolean(KEY_CHECKBOX_STATE, false)
    }

    override fun saveUserId(id: Int) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, id)
        }.apply()
    }

    override fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, 0)
    }

    override fun saveAccessToken(accessToken: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
        }.apply()
    }

    override fun getAccessToken(): String {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, DEFAULT_VALUE).toString()
    }

    override fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit().apply {
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }.apply()
    }

    override fun getRefreshToken(): String {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, DEFAULT_VALUE).toString()
    }

    override fun saveBigUserData(name: String, career: String, address: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_CAREER, career)
            putString(KEY_USER_ADDRESS, address)
        }.apply()
    }

    override fun getUserDataForUI(): List<String> {
        return listOf(
            sharedPreferences.getString(KEY_USER_NAME, DEFAULT_VALUE).toString(),
            sharedPreferences.getString(KEY_USER_CAREER, DEFAULT_VALUE).toString(),
            sharedPreferences.getString(KEY_USER_ADDRESS, DEFAULT_VALUE).toString()
        )
    }

    override fun clearStorageData() {
        sharedPreferences.edit().apply {
            clear()
        }.apply()
    }

//    fun getPref(): SharedPreferences {
//        return sharedPreferences
//    }

    companion object {
        const val PREFERENCES_NAME = "UserDataStorage"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
        const val KEY_CHECKBOX_STATE = "checkboxState"

        const val KEY_USER_ID = "userId"
        const val KEY_ACCESS_TOKEN = "accessToken"
        const val KEY_REFRESH_TOKEN = "refresh_token"

        const val KEY_USER_NAME = "name"
        const val KEY_USER_CAREER = "career"
        const val KEY_USER_ADDRESS = "address"
        const val DEFAULT_VALUE = "noData"

    }
}