package com.fyp.smartbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.fyp.smartbus.databinding.ActivityDriverBinding
import com.fyp.smartbus.utils.*
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * Driver's main screen
 */
class DriverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverBinding
    private var locationStopReceiver: LocationStoppedReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabDriving.setOnClickListener {
            // 1.1 Check location permission
            if (hasLocationPermission()) {
                // 1.2 Check GPS is on
                if (isLocationEnabled())
                    startDriving()
                else
                    enableGPS(MapsUtils.getLocationRequest())
            } else {
                requestForegroundPermissions()
            }
        }

        locationStopReceiver = LocationStoppedReceiver()
    }

    private fun startDriving() {
        sharedPref.edit(true) {
            putBoolean(KEY_IS_DRIVING, true)
        }
        val intent = Intent(this, DrivingActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun requestForegroundPermissions() {
        val provideRationale = hasLocationPermission()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                binding.container,
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) { makePermissionRequest() }
                .show()
        } else {
            makePermissionRequest()
        }
    }

    // Review Permissions: Handles permission result.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    println("ffnet: User interaction was cancelled.")

                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission was granted.
                    // TODO: Change

                } else -> {

                    Snackbar.make(
                        binding.container,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationStopReceiver?.let {
            registerReceiver(it, IntentFilter(ACTION_STOP_LOCATION))
        }
        // IF already location service is ON then the button shouldn't appear
        val isDriving = sharedPref.getBoolean(KEY_IS_DRIVING, false)
        if(isDriving)
            binding.fabDriving.invisible()
        else
            binding.fabDriving.visible()
    }

    override fun onPause() {
        locationStopReceiver?.let {
            unregisterReceiver(it)
        }
        super.onPause()
    }

    private inner class LocationStoppedReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            binding.fabDriving.visible()
            log("Driving visible")
        }
    }
}