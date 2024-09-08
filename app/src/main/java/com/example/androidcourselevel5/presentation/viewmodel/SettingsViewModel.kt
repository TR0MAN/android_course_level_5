package com.example.androidcourselevel5.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.androidcourselevel5.data.repository.SharedPrefStorage
import com.example.androidcourselevel5.domain.repository.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val storage: DataStorage
): ViewModel() {

    fun getDataForSetToUI(): List<String> {
        return storage.getUserDataForUI()
    }

    fun getCheckboxState() = storage.getCheckboxState()

    fun clearStorageData(){
        storage.clearStorageData()
    }
}