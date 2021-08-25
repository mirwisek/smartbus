package com.fyp.privacyguard.login.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.R
import com.fyp.smartbus.api.NetworkFactory
import com.fyp.smartbus.api.User
import com.fyp.smartbus.login.model.LoginFormState

class LoginViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _loginForm = MutableLiveData(LoginFormState())
    val loginFormState: LiveData<LoginFormState> = _loginForm
    val progressVisibility = MutableLiveData(false)


    fun login(email: String, password: String, onResult: (Result<User>) -> Unit) {
        NetworkFactory.login(User(email, password), onResult)
    }

//    fun forgetPassword(email: String): LiveData<UserResult<Boolean>> {
//        val forgetResult = MutableLiveData<UserResult<Boolean>>()
//        repository.forgetPassword(email) { result ->
//            if (result is Result.Success) {
//                forgetResult.value = UserResult(success = true)
//            } else if(result is Result.Error) {
//                forgetResult.value = UserResult(error = result.exception)
//            }
//        }
//        return forgetResult
//    }

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value =
                LoginFormState(emailError = R.string.invalid_email, isEligibleForgetPassword = false)
        } else if (!isPasswordValid(password)) {
            _loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password, isEligibleForgetPassword = true)
        } else {
            _loginForm.value =
                LoginFormState(isDataValid = true, isEligibleForgetPassword = true)
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
}