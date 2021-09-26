package com.fyp.smartbus.ui

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fyp.smartbus.R
import com.fyp.smartbus.databinding.ActivityAdminBinding
import com.fyp.smartbus.ui.admin.UsersAdapter
import com.fyp.smartbus.utils.toast
import com.fyp.smartbus.viewmodel.AdminViewModel

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_admin)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_users, R.id.navigation_buses
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val vmAdmin = ViewModelProvider(this).get(AdminViewModel::class.java)

        val adapter = UsersAdapter(this) { updateUser ->
            vmAdmin.updateUser(updateUser.email) { isSuccess, error ->
                if(isSuccess) {
                    // update list
//                    binding.progress.visible()
                    vmAdmin.getAllUsers()
//                    binding.rvUsers.invisible()
                    toast("User Verified successfully!")
                } else {
                    toast("Error: $error")
                }
            }
        }

        vmAdmin.getAllUsers()

        vmAdmin.usersList.observe(this) { list ->
            if(list.isEmpty()) {
                toast("No user account found...")
            } else {
                toast("Some Error to getting acounts")
                adapter.updateList(list)
            }
        }

//        vmAdmin.error.observe(this) { e ->
//            if(e == null) {
//                binding.error.invisible()
//            } else {
//                binding.error.apply {
//                    error = e.message
//                    visible()
//                }
//                e.printStackTrace()
//            }
//        }
    }





}