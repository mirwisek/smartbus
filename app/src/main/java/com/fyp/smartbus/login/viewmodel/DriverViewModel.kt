package com.fyp.smartbus.login.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fyp.smartbus.api.User
import com.fyp.smartbus.utils.*
import kotlinx.coroutines.launch

class DriverViewModel :ViewModel() {

    val isDriving = MutableLiveData<Boolean>(false)

}