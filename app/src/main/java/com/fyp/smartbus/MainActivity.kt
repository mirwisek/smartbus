package com.fyp.smartbus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fyp.smartbus.api.app.Bus
import com.fyp.smartbus.login.RegistrationActivity
import com.fyp.smartbus.login.viewmodel.BusListViewModel
import com.fyp.smartbus.ui.buses.BusListFragmentDirections
import com.fyp.smartbus.ui.home.HomeFragment
import com.fyp.smartbus.ui.home.HomeFragmentArgs
import com.fyp.smartbus.utils.sharedPref
import com.fyp.smartbus.utils.toLatLng
import com.fyp.smartbus.utils.toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSION_LOCATION = 4251
        const val KEY_USER_SAVED_LOCATION = "userLocationSvd"

        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private val locationRequest = LocationRequest.create().apply {
            interval = 2_000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    var markerCurrentLoc: Marker? = null
    private var mMap: GoogleMap? = null
    lateinit var fusedApi: FusedLocationProviderClient
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var timer: Timer? = null
    private lateinit var vmBusList: BusListViewModel
    private lateinit var navController: NavController
    var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_buses),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        vmBusList = ViewModelProvider(this).get(BusListViewModel::class.java)


        fusedApi = LocationServices.getFusedLocationProviderClient(this)
        if(hasPermissions(*permissions)) {
            onPermissionGranted()
        } else {
            requestPermissions()
        }
    }

    fun addCurrentLocationMarker() {
        currentLocation?.let {
            markerCurrentLoc?.remove()
            markerCurrentLoc = mMap?.addMarker(
                MarkerOptions()
                    .position(it)
                    .flat(true)
                    .title("Your location")
            )
        }
    }

    fun directionsOnHome(bus: Bus) {
        currentLocation?.let {
            vmBusList.selectedBus.value = bus
            vmBusList.getDirections(it)
            val action = BusListFragmentDirections.actionShowMapDirections(bus.busno!!)
            navController.navigate(action)
        }
    }

    fun showBusOnMap(bus: Bus) {
        val loc = bus.currentloc ?: bus.lastloc
        val action = BusListFragmentDirections.actionShowBus(busLocation = loc)
        navController.navigate(action)
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

    private fun newBusesFetchTask(): TimerTask {
        return object: TimerTask() {
            override fun run() {
                vmBusList.getAllBuses()
            }
        }
    }

    private fun signOut() {
        sharedPref.edit().clear().apply()
        startLogin()
    }

    override fun onResume() {
        super.onResume()

        timer = Timer().apply {
            scheduleAtFixedRate(newBusesFetchTask(), 100L, 5000L)
        }
    }

    override fun onPause() {
        timer?.cancel()
        super.onPause()
    }

    fun onMapReady(mMap: GoogleMap) {
        this.mMap = mMap
    }

    private fun startLogin() {
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("MissingPermission")
    fun onPermissionGranted() {
        if (hasPermissions(*permissions)) {
            // Get last location
            // Before retrieving location check if gps is enabled
            if (isLocationEnabled())
                forceLocation()
            else
                enableGPS(locationRequest) {
                    forceLocation()
                }
        } else
            toast("No location permission granted")
    }

    @SuppressLint("MissingPermission")
    private fun forceLocation() {
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
        currentLocation = LatLng(location.latitude, location.longitude)

        val position = CameraPosition.builder()
            .target(currentLocation!!)
            .zoom(20f)
            .build()

        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position))
        addCurrentLocationMarker()
    }

    //region Permission Handling
    fun requestPermissions() {
        // Permission is not granted
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(
            this,
            permissions,
            REQUEST_PERMISSION_LOCATION
        )
    }

    private fun hasPermissions(vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
}