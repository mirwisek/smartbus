package com.fyp.smartbus

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fyp.smartbus.api.ApiHelper
import com.fyp.smartbus.api.Bus
import com.fyp.smartbus.api.User
import com.fyp.smartbus.utils.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth


class DriverLocationService : Service() {
    val TAG = "ffnet::DriverLocationService"
    private var configurationChange = false

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    // LocationRequest - Requirements for the location updates, i.e., how often you should receive
    // updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private lateinit var locationCallback: LocationCallback



    // TODO: Step 1.1, Review variables (no changes).
    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    // Used only for local storage of the last known location. Usually, this would be saved to your
    // database, but because this is a simplified sample without a full database, we only need the
    // last location to create a Notification if the user navigates away from the app.
    private var currentLocation: Location? = null
    private var prevLocation: Location? = null
    private var driver: User? = null



    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {

        // Initialize driver here for location update retrofit requests
        applicationContext.sharedPref.apply {
            val email = getString(KEY_EMAIL, null)
            val username = getString(KEY_USERNAME, null)
            if (email != null && username != null) {
                driver = User(email, "", username)
            }
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(serviceRunningInForeground)
            notificationManager.notify(NOTIFICATION_ID, generateNotification())

        // TODO: Step 1.2, Review the FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // TODO: Step 1.3, Create a LocationRequest.
        locationRequest = MapsUtils.getLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val location = locationResult.locations ?: return
                val loc = location[0]
                prevLocation = currentLocation
                currentLocation = loc // current Location

                // Send broadcast, for DriverActivity to update UI
                Intent().also { intent ->
                    intent.action = ACTION_LOCATION_BROADCAST
                    intent.putExtra(EXTRA_LOCATION, loc)
                    sendBroadcast(intent)
                }

                // TODO: [Zain - When other errors fixed then uncomment the following function]
                updateLocationDatabase(loc.toLatLng())
            }
        }

    }

    private fun updateLocationDatabase(loc: LatLng, isOnline: Boolean = true, cb: (() -> Unit)? = null) {
        driver?.let { u ->
            val bus = Bus(u.email, currentloc = loc.string())
            ApiHelper.updateBus(bus) { result ->
                result.fold(
                    onSuccess = {
                        log("Location updated success")
                        cb?.invoke()
                    },
                    onFailure = {
                        log("Location failed due to ${it.message}")
                        cb?.invoke()
                    }
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates()

            // TODO: [Zain - Uncomment this finally, turn off isonline && clear locations, should set location to null when isonline turned off]
            updateLocationDatabase(LatLng(0.0, 0.0), isOnline = false) {
                toast("Going Offline...")
                stopSelf()
            }
        }
        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")

        // MainActivity (client) comes into foreground and binds to service, so the service can
        // become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")

        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")

        // MainActivity (client) leaves foreground, so service needs to become a foreground service
        // to maintain the 'while-in-use' label.
        // NOTE: If this method is called due to a configuration change in MainActivity,
        // we do nothing.
        if (!configurationChange) {

            val notification = generateNotification()
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }

        // Ensures onRebind() is called if MainActivity (client) rebinds.
        return true
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")
        // TODO: Chaqnge driverUID to driver email
//        saveLocationTrackingPref( true,
//            currentLocation.toText(), driverUid!!)

        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).
        val service = Intent(applicationContext, DriverLocationService::class.java)
        startService(service)

        try {
            // TODO: Step 1.5, Subscribe to location changes.
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()!!)

        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }


    fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")

        try {
            // TODO: Step 1.6, Unsubscribe to location changes.
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }
//            saveLocationTrackingPref( false,
//                currentLocation.toText(), driverUid!!)

        } catch (unlikely: SecurityException) {
//            saveLocationTrackingPref( true,
//                currentLocation.toText(), driverUid!!)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that represent latest location.
     */
    private fun generateNotification(): Notification {
        Log.d(TAG, "generateNotification()")

        val mainNotificationText = "Driving status: ON"

        val titleText = getString(R.string.app_name)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        // TODO: Change to driver acitivty
        // 3. Set up main Intent/Pending Intents for notification.
        val launchActivityIntent = Intent(this, DrivingActivity::class.java)

        val cancelIntent = Intent(this, DriverLocationService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
            this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChanel()
        }

        // 4. Build and issue the notification.
        // Notification Channel Id is ignored for Android pre O (26).
        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(activityPendingIntent)
//            .addAction(
//                R.drawable.ic_bus, getString(R.string.launch_driverActivity),
//                activityPendingIntent
//            )
            .addAction(R.drawable.ic_check, getString(R.string.stop_broadcasting), servicePendingIntent)
            .build()
    }



    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal val service: DriverLocationService
            get() = this@DriverLocationService
    }


}