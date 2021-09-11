package com.fyp.smartbus.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.smartbus.R
import com.fyp.smartbus.databinding.ActivityAdminBinding
import com.fyp.smartbus.databinding.FragmentBusListBinding
import com.fyp.smartbus.login.viewmodel.AdminViewModel
import com.fyp.smartbus.ui.admin.UsersAdapter
import com.fyp.smartbus.utils.*
import java.util.*

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val vmAdmin = ViewModelProvider(this).get(AdminViewModel::class.java)

        val adapter = UsersAdapter(this) { deletedUser ->
            vmAdmin.deleteUser(deletedUser.email) { isSuccess, error ->
                if(isSuccess) {
                    // update list
                    binding.progress.visible()
                    vmAdmin.getAllUsers()
                    binding.rvUsers.invisible()
                    toast("User deleted successfully!")
                } else {
                    toast("Error: $error")
                }
            }
        }

        binding.rvUsers.apply {
            this.adapter = adapter
        }

        binding.progress.visible()
        vmAdmin.getAllUsers()

        vmAdmin.usersList.observe(this) { list ->
            if(list.isEmpty()) {
                binding.error.apply {
                    text = "There are no user accounts!"
                    visible()
                }
                binding.progress.invisible()
            } else {
                binding.progress.invisible()
                binding.error.invisible()
                binding.rvUsers.visible()
                adapter.updateList(list)
            }
        }

        vmAdmin.error.observe(this) { e ->
            if(e == null) {
                binding.error.invisible()
            } else {
                binding.error.apply {
                    error = e.message
                    visible()
                }
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun signOut() {
        sharedPref.edit().clear().apply()
        switchActivity(RegistrationActivity::class.java)
    }
}