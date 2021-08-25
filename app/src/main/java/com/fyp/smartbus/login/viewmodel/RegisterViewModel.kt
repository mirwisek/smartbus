package com.fyp.smartbus.login.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel

import com.fyp.smartbus.R
import com.fyp.smartbus.api.NetworkFactory
import com.fyp.smartbus.api.User
import com.fyp.smartbus.login.model.LoginFormState
import com.fyp.smartbus.login.model.RegisterFormState
import com.fyp.smartbus.utils.log

class RegisterViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm
    val progressVisibility = MutableLiveData<Boolean>(false)

//    private val _registerResult = MutableLiveData<UserResult<LoggedInUserView>>()
//    val registerResult: LiveData<UserResult<LoggedInUserView>> = _registerResult

    fun signUp(email: String, password: String, username: String, usertype:String, onResult: (Result<User>) -> Unit) {
        NetworkFactory.signup(User(email, password, username, usertype), onResult)
    }

//    fun register(user: LoggedInUser) {
//        // can be launched in a separate asynchronous job
//        repository.register(user) { result ->
//            if (result is Result.Success) {
//                val returnedUser = result.data
//                SharedPrefsHelper.saveUser(app.applicationContext, returnedUser)
//
//                _registerResult.value =
//                    UserResult(
//                        success = LoggedInUserView(
//                            displayName = returnedUser.name!!
//                        )
//                    )
//            } else if(result is Result.Error) {
//                _registerResult.value = UserResult(error = result.exception)
//            }
//        }
//    }

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
        }else {
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
}