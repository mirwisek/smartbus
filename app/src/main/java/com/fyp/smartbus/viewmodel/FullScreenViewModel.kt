package com.fyp.smartbus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.api.app.User
import com.fyp.smartbus.utils.*

class FullScreenViewModel(private val app: Application) :
    AndroidViewModel(app) {

    private val _loggedUser = MutableLiveData<User?>()
    val loggedUser: LiveData<User?> = _loggedUser

    val progressVisibility = MutableLiveData<Boolean>(false)

    fun loadUserDetails() {
        app.applicationContext.sharedPref.apply {
            val email = getString(KEY_EMAIL, null)
            val username = getString(KEY_USERNAME, null)
            val usertype = getString(KEY_USERTYPE, null)
            if (email != null && username != null && usertype != null) {
                _loggedUser.value = User(email, "", username, usertype = usertype)
            }
        }
    }

}