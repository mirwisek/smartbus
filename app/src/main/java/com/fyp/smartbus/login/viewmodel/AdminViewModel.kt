package com.fyp.smartbus.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.api.app.ApiHelper
import com.fyp.smartbus.api.app.Bus
import com.fyp.smartbus.api.app.User
import com.fyp.smartbus.api.directions.DirectionResult
import com.fyp.smartbus.api.directions.DirectionsHelper
import com.fyp.smartbus.api.directions.Route
import com.fyp.smartbus.utils.log
import com.fyp.smartbus.utils.string
import com.google.android.gms.maps.model.LatLng

class AdminViewModel(private val app: Application) : AndroidViewModel(app)  {

    val usersList = MutableLiveData<List<User>>(listOf())
    val error = MutableLiveData<Throwable?>(null)

    fun getAllUsers() {
        ApiHelper.getAllUsers { result ->
            result.fold(
                onSuccess = {
                    usersList.postValue(it)
                    error.postValue(null)
                },
                onFailure = {
                    usersList.postValue(listOf())
                    error.postValue(it)
                }
            )
        }
    }

    fun deleteUser(email: String, res: (isSuccess: Boolean, error: String?) -> Unit) {

        ApiHelper.deleteUser(email) { result ->
            result.fold(
                onSuccess = {
                    res.invoke(true, null)
                },
                onFailure = {
                    res.invoke(false, it.message)
                }
            )
        }
    }

}