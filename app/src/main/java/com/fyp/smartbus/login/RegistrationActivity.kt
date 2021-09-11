package com.fyp.smartbus.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fyp.smartbus.DriverActivity
import com.fyp.smartbus.MainActivity
import com.fyp.smartbus.R
import com.fyp.smartbus.login.viewmodel.FullScreenViewModel
import com.fyp.smartbus.utils.switchActivity

class RegistrationActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 1110
//        const val KEY_IS_NEW_USER = "isNewUser"
//        const val KEY_USER_TYPE = "userType"
//        const val KEY_IS_ADMIN_LOGGED_IN = "adminLogin"
    }

    lateinit var vmFullScreen: FullScreenViewModel

    override fun onStart() {
        super.onStart()

        vmFullScreen = ViewModelProvider(this).get(FullScreenViewModel::class.java)
        vmFullScreen.loadUserDetails()
        vmFullScreen.loggedUser.observe(this) { user ->
            user?.let {
                when (it.usertype) {
                    "D" -> switchActivity(DriverActivity::class.java)
                    "A" -> switchActivity(AdminActivity::class.java)
                    else -> switchActivity(MainActivity::class.java)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        val fragLogin =
            supportFragmentManager.findFragmentByTag(LoginFragment.TAG)
                ?: LoginFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragLogin, LoginFragment.TAG)
            .commit()

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

}