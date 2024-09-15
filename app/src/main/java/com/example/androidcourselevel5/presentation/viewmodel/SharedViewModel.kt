package com.example.androidcourselevel5.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    val tabLayoutVisibility = MutableLiveData<Boolean>(true)

}