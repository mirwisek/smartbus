package com.fyp.smartbus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.fyp.smartbus.utils.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val REQUEST_PERMISSION_LOCATION = 4251
        const val REQUEST_CHECK_SETTINGS = 3422
        const val KEY_USER_SAVED_LOCATION = "userLocationSvd"

        private val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private val locationRequest = LocationRequest.create().apply {
            interval = 2_000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
    private lateinit var fusedApi: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        isLocationEnabled()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fusedApi = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(this, *permissions)) {
                requestPermissions()
            } else {
                onPermissionGranted()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun onPermissionGranted() {
        if (hasPermissions(this, *permissions)) {
            // Get last location
            fusedApi.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    // If last location isn't returned, make a new request
                    if (location == null) {
                        // Before retrieving location check if gps is enabled
                        if (isLocationEnabled())
                            forceLocation()
                        else
                            enableGPS()
                    } else {
                        onLocationReceived(location)
                    }
                } else {
                    toast("Your location couldn't be determined")
                }
            }
        } else
            toast("No location permission granted")
    }

    @SuppressLint("MissingPermission")
    private fun forceLocation() {
        isLocationEnabled()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                onLocationReceived(result.locations[0])
                fusedApi.removeLocationUpdates(this)
            }
        }
        fusedApi.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
    }

    /**
     * Method to perform operation on Location
     */
    private fun onLocationReceived(location: Location) {

        val loc = LatLng(location.latitude, location.longitude)
        val position = CameraPosition.builder()
            .target(loc)
            .zoom(20f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    //region Permission Handling
    private fun requestPermissions() {
        // Permission is not granted
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_LOCATION)
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_LOCATION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    toast("Permission denied!")
                } else {
                    onPermissionGranted()
                }
            }
        }
    }
    //endregion Permission Handling

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    forceLocation()
                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    //region Turn On GPS if it's OFF
    private fun enableGPS(callback: ((LocationSettingsResponse) -> Unit)? = null) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())


        task.addOnSuccessListener { callback?.invoke(it) }

        // This shows the Alert Dialog to display user to enable GPS
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            }
        }
    }
    //endregion
}