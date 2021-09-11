package com.fyp.smartbus.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fyp.smartbus.R
import com.fyp.smartbus.viewmodel.RegisterViewModel
import com.fyp.smartbus.ui.CheckMaterialButton
import com.fyp.smartbus.utils.*
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegistrationFragment : Fragment() {

    companion object {
        const val TAG = "RegistrationFragment"
    }

    private lateinit var vmRegistration: RegisterViewModel
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPass: TextInputEditText
    private lateinit var etUserName: TextInputEditText
    private lateinit var etBus: TextInputEditText
    private lateinit var btnStudentType: CheckMaterialButton
    private lateinit var tlbus: TextInputLayout
    private lateinit var btnDriverType: CheckMaterialButton
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vmRegistration = ViewModelProvider(this).get(RegisterViewModel::class.java)

        etEmail = view.findViewById(R.id.etemailregister)
        etPass = view.findViewById(R.id.etpassregister)
        etUserName = view.findViewById(R.id.etusernameregister)
        etBus = view.findViewById(R.id.etbus)
        btnStudentType = view.findViewById(R.id.btnStudent)
        btnDriverType = view.findViewById(R.id.btnDriver)
        btnRegister = view.findViewById(R.id.btnregister)

        val tlBus = view.findViewById<TextInputLayout>(R.id.tlbus)
        val tlNameRegister = view.findViewById<TextInputLayout>(R.id.tlnameregister)

        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleType)
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(checkedId == btnStudentType.id) {
                tlBus.invisible()
                tlNameRegister.hint = "Student Name"
            } else {
                tlBus.visible()
                tlNameRegister.hint = "Driver Name"

            }
        }

        btnStudentType.isChecked = true // initial selection
        progressBar = view.findViewById(R.id.progress)

        view.findViewById<TextView>(R.id.tvSignIn).setOnClickListener {

            val fragLogin =
                activity?.supportFragmentManager?.findFragmentByTag(LoginFragment.TAG)
                    ?: LoginFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, fragLogin, LoginFragment.TAG)
                ?.commit()
        }
        btnRegister.setOnClickListener {
            registerUser()
        }

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
                vmRegistration.registerDataChanged(
                    etEmail.text.toString(),
                    etPass.text.toString(),
                    etUserName.text.toString(),
                    etBus.text.toString(),
                    btnDriverType.isChecked
                )
            }
        }

        vmRegistration.registerFormState.observe(viewLifecycleOwner,
            Observer { registerFormState ->
                if (registerFormState == null) {
                    return@Observer
                }
                registerFormState.emailError?.let {
                    etEmail.error = getString(it)
                }
                registerFormState.passwordError?.let {
                    etPass.error = getString(it)
                }
                registerFormState.nameError?.let {
                    etUserName.error = getString(it)
                }
                registerFormState.busError?.let {
                    etBus.error = getString(it)
                }
            })

        etEmail.addTextChangedListener(afterTextChangedListener)
        etUserName.addTextChangedListener(afterTextChangedListener)
        etPass.addTextChangedListener(afterTextChangedListener)
        etBus.addTextChangedListener(afterTextChangedListener)

    }

    private fun registerUser() {
        vmRegistration.registerDataChanged(
            etEmail.text.toString(),
            etPass.text.toString(),
            etUserName.text.toString(),
            etBus.text.toString(),
            btnDriverType.isChecked
        )
        log("Reg: ${vmRegistration.registerFormState.value?.toString()}")
        if (vmRegistration.registerFormState.value?.isDataValid == true) {
            toggleFormInput(false)
            showProgress()

            val email = etEmail.text.toString()
            val password = etPass.text.toString()
            val username = etUserName.text.toString()
            val busno = if (btnStudentType.isChecked) null else etBus.text.toString()
//            toast(busno)
//            val usertype = etUserType.text.toString()
            val usertype = if (btnStudentType.isChecked) "S" else "D"
//            toast(usertype)
            vmRegistration.signUp(email, password, username, busno, usertype) { result ->
                result.fold(
                    onSuccess = { u ->
                        hideProgress()
                        toggleFormInput(true)
//
                        val fragLogin =
                            activity?.supportFragmentManager?.findFragmentByTag(LoginFragment.TAG)
                                ?: LoginFragment()
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, fragLogin, LoginFragment.TAG)
                            ?.commit()
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

    private fun toggleFormInput(enabled: Boolean) {
        btnRegister.isEnabled = enabled
//        forgotPassword.isEnabled = enabled
//        signup.isEnabled = enabled
    }

    private fun showProgress() {
        progressBar.visible()
    }

    private fun hideProgress() {
        progressBar.gone()
    }

//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = "Registration success: " + model.displayName
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
//        moveToPage(Pages.LOGIN)
//    }
//
//    private fun showRegistrationFailed(errorString: String) {
//        hideProgress()
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
//    }
//
//    override fun onDestroyView() {
//        binding = null
//        super.onDestroyView()
//    }
//
//    private fun moveToPage(page: Pages) {
//        (requireActivity() as RegistrationActivity).moveToPage(page)
//    }


}