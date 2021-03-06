package com.fyp.smartbus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.api.app.ApiHelper
import com.fyp.smartbus.api.app.User

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

    fun verifyUser(email: String, res: (isSuccess: Boolean, error: String?) -> Unit) {

        ApiHelper.verifyUser(email) { result ->
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