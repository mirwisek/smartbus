package com.fyp.smartbus.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fyp.privacyguard.login.viewmodel.LoginViewModel
import com.fyp.smartbus.DriverActivity
import com.fyp.smartbus.MainActivity
import com.fyp.smartbus.R
import com.fyp.smartbus.utils.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore


class AdminLoginFragment : Fragment() {

    companion object {
        const val TAG = "AdminFragmentLogin"
    }

    private lateinit var vmLogin: LoginViewModel
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPass: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

//    private var callback: (() -> Unit?)? = null
//    private val db = FirebaseFirestore.getInstance()
//    private var pass = "NA"
//    private var error: String? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val inflater = TransitionInflater.from(requireContext())
//        exitTransition = inflater.inflateTransition(R.transition.fade)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        vmLogin = ViewModelProvider(this).get(LoginViewModel::class.java)

        progressBar = view.findViewById(R.id.loading)
        etPass = view.findViewById(R.id.etpass)
        etEmail = view.findViewById(R.id.etemail)
        btnLogin = view.findViewById(R.id.btnlogin)


        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                vmLogin.loginDataChanged(
                    etEmail.text.toString(),
                    etPass.text.toString()
                )
            }
        }


        vmLogin.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
//                if (loginFormState.isEligibleForgetPassword)
//                    bind.forgotPassword.setTextColor(requireContext().getColorCompat(R.color.light_blue_900))
//                else
//                    bind.forgotPassword.setTextColor(requireContext().getColorCompat(R.color.black))

                loginFormState.emailError?.let {
                    etEmail.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    etPass.error = getString(it)
                }
            })
        etPass.addTextChangedListener(afterTextChangedListener)

        view.findViewById<TextView>(R.id.btnregistration).setOnClickListener {
            val fragRegistration =
                activity?.supportFragmentManager?.findFragmentByTag(RegistrationFragment.TAG)
                    ?: RegistrationFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragRegistration, RegistrationFragment.TAG)
                ?.commit()
        }

        btnLogin.setOnClickListener {
//            toast(vmLogin.loginFormState.value?.isDataValid.toString())
            if (vmLogin.loginFormState.value?.isDataValid == true) {
                toggleFormInput(false)
                showProgress()
                val email = etEmail.text.toString()
                val pass = etPass.text.toString()
                vmLogin.login(email, pass) { result ->
//                    log("inside login...$result")
                    result.fold(
                        onSuccess = { u ->
                            log("Savving ... $u")
                            requireContext().sharedPref.edit(true) {
                                putString(KEY_USERNAME, u.username)
                                putString(KEY_EMAIL, u.email)
                                putString(KEY_USERTYPE, u.usertype)
                            }
                            hideProgress()
                            toggleFormInput(true)
                            switchActivity(DriverActivity::class.java)
                        },
                        onFailure = { e ->
                            toast("ERROR: ${e.localizedMessage}")
                            e.printStackTrace()
                            hideProgress()
                            toggleFormInput(true)
                        }
                    )

                }
            } else {
                toast(getString(R.string.incorrect_fields))
            }
        }

    }

    private fun toggleFormInput(enabled: Boolean) {
        btnLogin.isEnabled = enabled
//        forgotPassword.isEnabled = enabled
//        signup.isEnabled = enabled
    }

    private fun showProgress() {
        progressBar.visible()
    }

    private fun hideProgress() {
        progressBar.gone()
    }
}
