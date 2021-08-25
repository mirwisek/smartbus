package com.fyp.smartbus.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.IdpResponse
import com.fyp.smartbus.MainActivity
import com.fyp.smartbus.R
import com.fyp.smartbus.login.viewmodel.FullScreenViewModel
import com.fyp.smartbus.utils.sharedPref
import com.fyp.smartbus.utils.switchActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers.Main

class RegistrationActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 1110
        const val KEY_IS_NEW_USER = "isNewUser"
        const val KEY_USER_TYPE = "userType"
        const val KEY_IS_ADMIN_LOGGED_IN = "adminLogin"
    }

    lateinit var vmFullScreen: FullScreenViewModel

    override fun onStart() {
        super.onStart()

        vmFullScreen = ViewModelProvider(this).get(FullScreenViewModel::class.java)
        vmFullScreen.loadUserDetails()
        vmFullScreen.loggedUser.observe(this) { user ->
            user?.let {
                switchActivity(MainActivity::class.java)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

//        val fragLogin =
//            supportFragmentManager.findFragmentByTag(AdminLoginFragment.TAG)
//                ?: AdminLoginFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, fragLogin, AdminLoginFragment.TAG)
//            .commit()
//        return


        val fragLogin =
            supportFragmentManager.findFragmentByTag(AdminLoginFragment.TAG)
                ?: AdminLoginFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragLogin, AdminLoginFragment.TAG)
            .commit()
//            val fragRegistration =
//                supportFragmentManager.findFragmentByTag(RegistrationFragment.TAG)
//                    ?: RegistrationFragment()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, fragRegistration, RegistrationFragment.TAG)
//                .commit()

    }

//    fun onLoginAsAdminClicked() {
//        val fragment =
//            supportFragmentManager.findFragmentByTag(AdminLoginFragment.TAG) ?: AdminLoginFragment()
//
//        (fragment as AdminLoginFragment).onLoginSuccess {
//            sharedPref.edit()
//                .putBoolean(KEY_IS_ADMIN_LOGGED_IN, true)
//                .apply()
//            onLoginFinish()
//        }
//        supportFragmentManager.beginTransaction()
//            .setCustomAnimations(R.anim.fui_slide_in_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
//            .replace(R.id.container, fragment, AdminLoginFragment.TAG)
//            .addToBackStack(LoginFragment.TAG)
//            .commit()
//
//    }

//    fun onLoginResult(response: IdpResponse?) {
//        response?.let { resp ->
//            sharedPref.edit()
//                .putBoolean(KEY_IS_NEW_USER, resp.isNewUser)
//                .apply()
//
//            if (resp.isNewUser) {
//                showUserSelection()
//            } else {
//                onLoginFinish()
//            }
//        }
//    }

//    private fun showUserSelection() {
//        val frag = (supportFragmentManager.findFragmentByTag(UserSelectionFragment.TAG)
//            ?: UserSelectionFragment()) as UserSelectionFragment
//        frag.onUserSelected { selected ->
//            onUserSelected(selected)
//        }
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, frag, UserSelectionFragment.TAG)
//            .commit()
//    }

//    private fun onLoginFinish() {
//        val intent = Intent(applicationContext, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    private fun onUserSelected(userType: String) {
//        sharedPref.edit()
//            .putString(KEY_USER_TYPE, userType)
//            .apply()
//        onLoginFinish()
//    }

}