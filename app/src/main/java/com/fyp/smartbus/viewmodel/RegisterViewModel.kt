package com.fyp.smartbus.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.R
import com.fyp.smartbus.api.app.ApiHelper
import com.fyp.smartbus.api.app.User
import com.fyp.smartbus.login.model.RegisterFormState
import com.fyp.smartbus.utils.log

class RegisterViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm
    val progressVisibility = MutableLiveData<Boolean>(false)

    fun signUp(
        email: String,
        password: String,
        username: String,
        busno: String?,
        usertype: String,
        onResult: (Result<User>) -> Unit
    ) {
        ApiHelper.signup(User(email, password, username, busno ,usertype), onResult)
    }

    fun registerDataChanged(email: String, password: String, username: String, busNo: String, isTypeDriver: Boolean) {
        log("After data changed $username $email $password $busNo $isTypeDriver")
        if (!isEmailValid(email)) {
            _registerForm.value =
                RegisterFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _registerForm.value =
                RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isNameValid(username)) {
            log("logic check ${isNameValid(username)} $username")
            _registerForm.value =
                RegisterFormState(nameError = R.string.invalid_name)
        } else if (isTypeDriver && !isBusNoValid(busNo)) { // Only consider busError when user type is driver
            _registerForm.value =
                RegisterFormState(busError = R.string.invalid_busno)
        } else {
            _registerForm.value =
                RegisterFormState(isDataValid = true)
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

    private fun isNameValid(username: String): Boolean {
        return username.length > 2
    }

    private fun isBusNoValid(busno: String): Boolean {
        return busno.length > 2
    }
}