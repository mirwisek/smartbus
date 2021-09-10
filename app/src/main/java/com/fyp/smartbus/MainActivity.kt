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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fyp.smartbus.login.AdminLoginFragment
import com.fyp.smartbus.login.viewmodel.BusListViewModel
import com.fyp.smartbus.utils.log
import com.fyp.smartbus.utils.sharedPref
import com.fyp.smartbus.utils.toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
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

    lateinit var fusedApi: FusedLocationProviderClient
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var timer: Timer? = null
    private lateinit var vmBusList: BusListViewModel

    private val busesFetchTask = object: TimerTask() {
        override fun run() {
            log("Called")
            vmBusList.getAllBuses()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_buses),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        vmBusList = ViewModelProvider(this).get(BusListViewModel::class.java)
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
        startLogin()
    }

    override fun onResume() {
        super.onResume()

        timer = Timer("busGetListTimer")
        timer!!.scheduleAtFixedRate(busesFetchTask, 100L, 5000L)
    }

    override fun onPause() {

        timer?.cancel()
        super.onPause()
    }

    fun onMapReady() {
        fusedApi = LocationServices.getFusedLocationProviderClient(this)
        if (!hasPermissions(this, *permissions)) {
            requestPermissions()
        } else {
            onPermissionGranted()
        }
    }

    private fun startLogin() {
        val fragLogin =
            supportFragmentManager.findFragmentByTag(AdminLoginFragment.TAG) ?: AdminLoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragLogin, AdminLoginFragment.TAG)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("MissingPermission")
    fun onPermissionGranted() {
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
                            enableGPS(locationRequest)
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
        // TODO: Set location to HomeFragment
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

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
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