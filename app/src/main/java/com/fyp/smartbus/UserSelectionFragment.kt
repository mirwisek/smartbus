package com.fyp.smartbus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class UserSelectionFragment : Fragment() {

    companion object {
        const val TAG = "UserSelectionFrag"
        const val STUDENT = "Student"
        const val DRIVER = "Driver"
    }

    private var callback: ((String) -> Unit?)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnDriver).setOnClickListener {
            callback?.invoke(DRIVER)
        }

        view.findViewById<Button>(R.id.btnStudent).setOnClickListener {
            callback?.invoke(STUDENT)
        }
    }

    fun onUserSelected(cb: (studentType: String) -> Unit) {
        callback = cb
    }

}