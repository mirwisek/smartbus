package com.fyp.smartbus.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fyp.smartbus.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import java.io.BufferedReader
import java.io.InputStreamReader

const val KEY_USERNAME = "username"
const val KEY_EMAIL = "email"
const val KEY_PASSWORD = "password"
const val KEY_USERTYPE = "usertype"
const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
const val LOCATION_LATLNG = "location_latlng"
const val DRIVER_UID = "driver_uid"
const val NOTIFICATION_CHANNEL_ID = "com.fyp.smartbus.channel"
const val NOTIFICATION_ID = 123

const val PACKAGE_NAME = "com.fyp.smartbus"

const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
    "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
    "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"
const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
/*
    * =========== KOTLIN - EXTENSIONS
*/

@RequiresApi(Build.VERSION_CODES.O)
fun Context.createNotificationChanel() {
    val channelName = "SmartBus Channel"
    val chan = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    chan.lightColor = Color.BLUE
    chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    val manager =
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    manager.createNotificationChannel(chan)
}

fun Activity.switchActivity(targetActivity: Class<*>) {
    val intent = Intent(this, targetActivity)
    startActivity(intent)
    this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    this.finish()
}

fun Fragment.switchActivity(targetActivity: Class<*>) {
    requireActivity().let { act ->
        val intent = Intent(act, targetActivity)
        startActivity(intent)
        act.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        act.finish()
    }

}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
//
//fun Context.drawRoute(map: GoogleMap): ArrayList<Polyline> {
//
//    val fileRoute = resources.readFile(R.raw.bus_route)
//    val json = JSONObject(fileRoute)
//    val quetta = json.getString("quetta")
//    val kuchlak = json.getString("kuchlak")
//
//    val opt1 = PolylineOptions()
//        .color(getMyColor(R.color.kuchlak))
//        .geodesic(true)
//        .width(20f)
//        .addAll(PolyUtil.decode(quetta))
//
//    val opt2 = PolylineOptions()
//        .color(getMyColor(R.color.quetta))
//        .geodesic(true)
//        .width(20f)
//        .addAll(PolyUtil.decode(kuchlak))
//
//    val polyQ = map.addPolyline(opt1)
//    val polyK = map.addPolyline(opt2)
//
//    return arrayListOf(polyQ, polyK)
//}


fun Context.isAboveMarshmallow(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

fun log(msg: String) {
    Log.i("ffnet", "😍😁 ❤❤ ==> $msg")
}

fun errLog(msg: String) {
    Log.e("ffnet", "❌⚔❌❌❌⛑☠ ==> $msg")
}

fun Fragment.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(msg, length)
}

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

//region Fragments
inline fun FragmentManager.transaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    // Can't call commitNow with AddToBackStack in the addFragment/replaceFragment extensions
    fragmentTransaction.commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
    supportFragmentManager.transaction {
        add(frameId, fragment, backStackTag)
        backStackTag?.let { addToBackStack(fragment.javaClass.name) }
    }
}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    backStackTag: String? = null
) {
    supportFragmentManager.transaction {
        replace(frameId, fragment, backStackTag)
        backStackTag?.let { addToBackStack(fragment.javaClass.name) }
    }
}
//endregion

fun String.toLatLng(): LatLng {
    val item = this.split(",")
    return LatLng(
        item[0].toDouble(),
        item[1].toDouble()
    )
}

fun LatLng.string(): String {
    return "$latitude,$longitude"
}

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun GeoPoint.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun Context.getMyColor(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

val Context.sharedPref: SharedPreferences
    get() = getSharedPreferences(getString(R.string.user_shared_prefs), Context.MODE_PRIVATE)


fun Resources.readFile(id: Int): String {
    val inputStream = openRawResource(id)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val sb = StringBuilder().apply {
        var line: String? = null
        while (reader.readLine().also { line = it } != null)
            append(line)
    }
    return sb.toString()
}


/*
*
* My Changed code
*
 */

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

fun Context.getLocationTrackingPref(): Boolean =
    sharedPref.getBoolean(KEY_FOREGROUND_ENABLED, false)

/**
 * Stores the location updates state in SharedPreferences.
 * @param requestingLocationUpdates The location updates state.
 */
fun Context.saveLocationTrackingPref(
    requestingLocationUpdates: Boolean,
    location: String,
    driverUID: String
) {
    sharedPref.edit {
        putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        putString(LOCATION_LATLNG, location)
        putString(DRIVER_UID, driverUID)
    }
}




