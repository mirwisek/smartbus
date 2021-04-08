package com.fyp.smartbus.login

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fyp.smartbus.R
import com.fyp.smartbus.utils.toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore


class AdminLoginFragment : Fragment() {

    companion object {
        const val TAG = "AdminFragmentLogin"
    }

    private var callback: (() -> Unit?)? = null
    private val db = FirebaseFirestore.getInstance()
    private var pass = "NA"
    private var error: String? = null

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

        val etPass = view.findViewById<TextInputEditText>(R.id.editTextPass)

        db.collection("users").document("admins")
            .get().addOnSuccessListener { doc ->
                pass = doc.getString("password") ?: "NA"
            }.addOnFailureListener {
                error = "Server Connectivity Error: ${it.message}"
                it.printStackTrace()
            }

        view.findViewById<Button>(R.id.btnLogin).setOnClickListener {
            etPass.text?.let {
                if(pass == "NA")
                    toast(error!!, Toast.LENGTH_LONG)
                else {
                    if(it.toString().compareTo(pass) == 0) {
                        callback?.invoke()
                    } else {
                        toast("The password provided is incorrect")
                    }
                }
            }
        }
    }

    fun onLoginSuccess(cb: () -> Unit) {
        callback = cb
    }

}