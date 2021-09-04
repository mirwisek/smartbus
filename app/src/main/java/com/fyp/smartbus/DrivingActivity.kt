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
import com.fyp.smartbus.databinding.ActivityDrivingBinding
import com.fyp.smartbus.utils.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil

/**
 * Activity to show maps
 */
class DrivingActivity : AppCompatActivity(), OnMapReadyCallback {

    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: DriverLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver? = null

    private lateinit var container: ViewGroup

    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as DriverLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
            // When service is binded then subscribe to locations
            foregroundOnlyLocationService?.subscribeToLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
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

    private lateinit var binding: ActivityDrivingBinding

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, DriverLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrivingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        foregroundOnlyBroadcastReceiver?.let {
            registerReceiver(it, IntentFilter(ACTION_LOCATION_BROADCAST))
        }
    }

    override fun onPause() {
        foregroundOnlyBroadcastReceiver?.let {
            unregisterReceiver(it)
        }
        super.onPause()
    }



    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission", "NewApi")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
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
            }
        }
    }
}