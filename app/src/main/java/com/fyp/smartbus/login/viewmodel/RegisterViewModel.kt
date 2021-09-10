package com.fyp.smartbus.login.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.R
import com.fyp.smartbus.api.app.ApiHelper
import com.fyp.smartbus.api.app.User
import com.fyp.smartbus.login.model.RegisterFormState

class RegisterViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm
    val progressVisibility = MutableLiveData<Boolean>(false)

//    private val _registerResult = MutableLiveData<UserResult<LoggedInUserView>>()
//    val registerResult: LiveData<UserResult<LoggedInUserView>> = _registerResult

    fun signUp(
        email: String,
        password: String,
        username: String,
        busno: String,
        usertype: String,
        onResult: (Result<User>) -> Unit
    ) {
        ApiHelper.signup(User(email, password, username, busno ,usertype), onResult)
    }

    fun registerDataChanged(email: String, password: String, username: String) {
        if (!isEmailValid(email)) {
            _registerForm.value =
                RegisterFormState(emailError = R.string.invalid_email)
//            Toast.makeText(getApplication(), "email:", Toast.LENGTH_SHORT).show()
        } else if (!isPasswordValid(password)) {
            _registerForm.value =
                RegisterFormState(passwordError = R.string.invalid_password)
//            Toast.makeText(getApplication(), "password:", Toast.LENGTH_SHORT).show()
        } else if (!isNameValid(username)) {
            _registerForm.value =
                RegisterFormState(nameError = R.string.invalid_name)
//            Toast.makeText(getApplication(), "name:", Toast.LENGTH_SHORT).show()
        } else {
            _registerForm.value =
                RegisterFormState(isDataValid = true)
//            Toast.makeText(getApplication(), "datavalid:", Toast.LENGTH_SHORT).show()
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isNameValid(name: String): Boolean {
        return name.length > 2
    }

//    private fun isBusNoValid(busno: String): Boolean {
//        return busno.length > 2
//    }
}