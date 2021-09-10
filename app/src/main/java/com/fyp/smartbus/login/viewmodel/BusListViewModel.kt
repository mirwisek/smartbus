package com.fyp.smartbus.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.api.ApiHelper
import com.fyp.smartbus.api.Bus

class BusListViewModel(private val app: Application) : AndroidViewModel(app)  {

    val busList = MutableLiveData<List<Bus>>(listOf())
    val error = MutableLiveData<Throwable?>(null)

    fun getAllBuses() {
        ApiHelper.getAllBuses { result ->
            result.fold(
                onSuccess = {
                    busList.postValue(it)
                    error.postValue(null)
                },
                onFailure = {
                    busList.postValue(listOf())
                    error.postValue(it)
                }
            )
        }
    }

}