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
import android.view.ViewGroup
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil

class DrivingActivity : AppCompatActivity() {

    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: DriverLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver? = null

    private lateinit var sharedPreferences: SharedPreferences
    private var cameraNavigated = false

    private var isDriving = false
    private lateinit var container: ViewGroup

    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as DriverLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
            log("Service connected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
            log("Service Disconnected")
        }
    }

    companion object {
        lateinit var locationViewModel: LocationViewModel
        const val KEY_IS_DRIVING = "isDrivingKey"
    }

    private lateinit var map: GoogleMap
    private var currentPositionMarker: Marker? = null

    //    private val markerAnimationHelper = MarkerAnimationHelper()
    private var prevLocation: Location? = null
    private var newLocation: Location? = null

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, DriverLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
        log("Start driving $isDriving")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        container = findViewById(R.id.container)

        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

//        sharedPreferences =
//            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
//
//        val enabled = sharedPreferences.getBoolean(KEY_FOREGROUND_ENABLED, false)

        foregroundOnlyLocationService?.subscribeToLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        foregroundOnlyBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver( it,
                IntentFilter(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST))
        }
    }

    override fun onPause() {
        println("ffnet: onPause()")
        foregroundOnlyBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        super.onPause()
    }



    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission", "NewApi")
    fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val locationProvider = LocationServices.getFusedLocationProviderClient(applicationContext)

        locationProvider.requestLocationUpdates(
            MapsUtils.getLocationRequest(),
            getLocationCallback(), Looper.myLooper()!!
        ).addOnSuccessListener {
            log("Locations")
        }

        // This part is crucial requireActvity works while this or byLazy don't
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
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
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                val location = p0.locations ?: return
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