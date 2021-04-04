package com.fyp.smartbus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.IdpResponse
import com.fyp.smartbus.utils.sharedPref
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 1110
        const val KEY_IS_NEW_USER = "isNewUser"
        const val KEY_USER_TYPE = "userType"
    }

    private var isNewUser = true

    override fun onStart() {
        super.onStart()

        FirebaseAuth.getInstance().currentUser?.let {
            isNewUser = false
            sharedPref.getString(KEY_USER_TYPE, null)?.let {
                onLoginFinish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        if(isNewUser) {
            val fragLogin =
                supportFragmentManager.findFragmentByTag(LoginFragment.TAG) ?: LoginFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragLogin, LoginFragment.TAG)
                .commit()
        } else {
            showUserSelection()
        }
    }

    fun onLoginResult(response: IdpResponse?) {
        response?.let { resp ->
            sharedPref.edit()
                .putBoolean(KEY_IS_NEW_USER, resp.isNewUser)
                .apply()

            if (resp.isNewUser) {
                showUserSelection()
            } else {
                onLoginFinish()
            }
        }
    }

    private fun showUserSelection() {
        val frag = (supportFragmentManager.findFragmentByTag(UserSelectionFragment.TAG)
            ?: UserSelectionFragment()) as UserSelectionFragment
        frag.onUserSelected { selected ->
            onUserSelected(selected)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, frag, UserSelectionFragment.TAG)
            .commit()
    }

    private fun onLoginFinish() {
        val intent = Intent(applicationContext, MapsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onUserSelected(userType: String) {
        sharedPref.edit()
            .putString(KEY_USER_TYPE, userType)
            .apply()
        onLoginFinish()
    }

}