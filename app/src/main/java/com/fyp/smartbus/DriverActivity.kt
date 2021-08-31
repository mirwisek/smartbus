package com.fyp.smartbus

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity.CENTER
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fyp.smartbus.utils.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil

class DriverActivity : AppCompatActivity() {

    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: DriverLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences
    private var cameraNavigated = false

    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as DriverLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {

            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    companion object {

        lateinit var locationViewModel: LocationViewModel
        lateinit var quettaRoute: MutableList<LatLng>
        lateinit var kuchlakRoute: MutableList<LatLng>
        const val TOLERANCE = 4.0
        const val RC_ACTIVITY_RESUMED: Int = 10900
    }

    private lateinit var map: GoogleMap
    private var currentPositionMarker: Marker? = null

    //    private val markerAnimationHelper = MarkerAnimationHelper()
    private var prevLocation: Location? = null
    private var newLocation: Location? = null
    private var driverUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        locationTrackInit()
    }

    override fun onStart() {
        super.onStart()

        val serviceIntent = Intent(this, DriverLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }

    override fun onPause() {
        println("ffnet: onPause()")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }


    private fun locationTrackInit() {
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val enabled = sharedPreferences.getBoolean(KEY_FOREGROUND_ENABLED, false)

        if (enabled) {
            foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
        } else {
            // TODO: Step 1.0, Review Permissions: Checks and requests if needed.
            if (foregroundPermissionApproved()) {
                foregroundOnlyLocationService?.subscribeToLocationUpdates()
                    ?: println("ffnet, Service Not Bound")
            } else {
                requestForegroundPermissions()
            }
        }

    }


    // TODO: Step 1.0, Review Permissions: Method checks if permissions approved.
    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // TODO: Step 1.0, Review Permissions: Method requests permissions.
    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                findViewById(R.id.map),
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@DriverActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            println("ffnet:, Request foreground only permission")
            ActivityCompat.requestPermissions(
                this@DriverActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // TODO: Step 1.0, Review Permissions: Handles permission result.
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

                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    // Permission was granted.
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()

                else -> {

                    Snackbar.make(
                        findViewById(R.id.map),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }
    /*
    *
    *
    *
    *
    * */


    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission", "NewApi")
    fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val locationProvider = LocationServices.getFusedLocationProviderClient(applicationContext)

        log("MAP READY")
        locationProvider.requestLocationUpdates(
            MapsUtils.getLocationRequest(),
            getLocationCallback(), Looper.myLooper()
        ).addOnSuccessListener {
            log("Locations")
        }

        // This part is crucial requireActvity works while this or byLazy don't
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
//        drawBoundaryCircle()

        quettaRoute = locationViewModel.getRoute()[0].points
        kuchlakRoute = locationViewModel.getRoute()[1].points
    }


    override fun onStop() {
        foregroundOnlyLocationService?.subscribeToLocationUpdates()
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }

        super.onStop()
    }

    /*
     * On Location updates do the following
     */
    private fun getLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                val location = p0?.locations ?: return
                val loc = location[0]
                prevLocation = newLocation
                newLocation = loc // current Location

//                driverUid?.let {
//                    if (PolyUtil.isLocationOnPath(loc.toLatLng(), quettaRoute, true, TOLERANCE))
//                        FirestoreUtils.postDriverLocation(loc, driverUid!!, Destination.QUETTA)
//                    else
//                        FirestoreUtils.postDriverLocation(loc, driverUid!!, Destination.KUCHLAK)
//                }
                // Can't proceed unless we have previous location available
//                prevLocation?.let {
//                    val bearing = MapsUtils.bearingBetweenLocations(prevLocation!!, newLocation!!)
////                    val n = LatLng(newLocation!!.latitude, newLocation!!.longitude)
////                    val p = LatLng(prevLocation!!.latitude, prevLocation!!.longitude)
//                    // Stop animating marker when speed it zero, this avoids marker rotating continuously
//                    if (newLocation!!.speed != 0F)
//                        animateMarker(bearing.toFloat(), prevLocation!!.bearing)
//                }

                // Navigate the camera to driver location once and don't do again because
                // if user has to move the map according to his choice then it will interfere
                if (!cameraNavigated) {
                    navigateToLocation(newLocation?.toLatLng()!!)
                    cameraNavigated = true
                }
            }
        }
    }


    /*
     * Draw boundary around the scrop of the buses stop
     * Given a location animate camera towards it
     */
    private fun navigateToLocation(loc: LatLng) {
        val position = CameraPosition.builder()
            .target(loc)
            .zoom(18f)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    /**
     *
     * Receiver for location broadcasts from [ForegroundOnlyLocationService].
     */
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            intent.getParcelableExtra<Location>(EXTRA_LOCATION)?.let { location ->

                navigateToLocation(location.toLatLng())
                println("ffnet: ${location.toText()}")
            }
            log("REceived broadcast")
        }
    }
}