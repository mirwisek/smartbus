package com.fyp.smartbus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.fyp.smartbus.databinding.ActivityDriverBinding
import com.fyp.smartbus.login.viewmodel.DriverViewModel
import com.fyp.smartbus.utils.*
import com.google.android.material.snackbar.Snackbar

/**
 * Driver's main screen
 */
class DriverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverBinding
    private var locationServiceStopReceiver: LocationServiceToggleReceiver? = null
    private lateinit var vmDriver: DriverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverBinding.inflate(layoutInflater)
        setContentView(binding.root)


        vmDriver = ViewModelProvider(this).get(DriverViewModel::class.java)

        binding.fabDriving.setOnClickListener {
            if(!vmDriver.isDriving.value!!) {
                // Check location permission
                if (hasLocationPermission()) {
                    // 1.2 Check GPS is on
                    if (isLocationEnabled())
                        startDriving()
                    else
                        enableGPS(MapsUtils.getLocationRequest())
                } else {
                    requestForegroundPermissions()
                }
            } else {
                binding.fabDriving.isEnabled = false
                binding.fabDriving.text = getString(R.string.stopping)
                // Stop Location tracking service
                Intent(this, DriverLocationService::class.java).also { intent ->
                    intent.putExtra(EXTRA_STOP_LOCATION_TRACKING, true)
                    startService(intent)
                }

            }
        }

        vmDriver.isDriving.observe(this) { isDriving ->
            updateDrivingStatus(isDriving)
        }

        locationServiceStopReceiver = LocationServiceToggleReceiver()
    }

    private fun updateDrivingStatus(isDriving: Boolean) {
        log("Status updated $isDriving")
        if(isDriving) {
            binding.fabDriving.text = getString(R.string.stop_driving)
        } else {
            binding.fabDriving.text = getString(R.string.start_driving)
            // After network request and service stopped then enable button
            binding.fabDriving.isEnabled = true
        }
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
                    // If user interaction was interrupted, thLocation failed due to timeoute permission request
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
        locationServiceStopReceiver?.let {
            registerReceiver(it, IntentFilter(ACTION_LOCTION_TOGGLED))
        }
        // IF already location service is ON then the button shouldn't appear
        vmDriver.isDriving.postValue((application as SmartBusApp).isDrivingServiceRunning)
//        val isDriving = sharedPref.getBoolean(KEY_IS_DRIVING, false)
//        vmDriver.isDriving.postValue(isDriving)
    }

    override fun onPause() {
        locationServiceStopReceiver?.let {
            unregisterReceiver(it)
        }
        super.onPause()
    }

    private inner class LocationServiceToggleReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val isDriving = intent.getBooleanExtra(EXTRA_IS_DRIVING, false)
            vmDriver.isDriving.postValue(isDriving)
            log("Driving turned: $isDriving")
        }
    }
}