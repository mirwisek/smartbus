package com.fyp.smartbus

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.fyp.smartbus.utils.MapsUtils
import com.fyp.smartbus.utils.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * Driver's main screen
 */
class DriverActivity : AppCompatActivity() {

    private lateinit var container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        container = findViewById(R.id.container)
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.fabDriving)

        fab.setOnClickListener {
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
    }

    private fun startDriving() {
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
                container,
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
        println("ffnet: onRequestPermissionResult")

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
                        container,
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
}