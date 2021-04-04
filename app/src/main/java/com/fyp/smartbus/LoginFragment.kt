package com.fyp.smartbus

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.fyp.smartbus.utils.toast

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnGetStated = view.findViewById<Button>(R.id.btnGetStarted)

        // Choose authentication providers
        val providers = arrayListOf(AuthUI.IdpConfig.PhoneBuilder().build())

        btnGetStated.setOnClickListener {
            // Create and launch sign-in intent
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(providers)
                    .setTheme(R.style.LoginTheme)
                    .build(),
                LoginActivity.RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LoginActivity.RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == AppCompatActivity.RESULT_OK) { // Successfully signed in
                (requireActivity() as LoginActivity).onLoginResult(response)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                toast("SignIn Failed!")
            }
        }
    }

}