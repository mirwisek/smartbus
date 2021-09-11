package com.fyp.smartbus.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData

class DriverViewModel :ViewModel() {

    val isDriving = MutableLiveData<Boolean>(false)

}