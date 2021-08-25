package com.fyp.smartbus.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

/*
    * =========== KOTLIN - EXTENSIONS
*/
fun Activity.switchActivity(targetActivity: Class<*>) {
    val intent = Intent(this, targetActivity)
    startActivity(intent)
    this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    this.finish()
}

fun Fragment.switchActivity(targetActivity: Class<*>){
    requireActivity().let {act ->
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
    Log.i("Custom Log", "😍😁 ❤❤ ==> $msg")
}

fun errLog(msg: String) {
    Log.e("Error Log", "❌⚔❌❌❌⛑☠ ==> $msg")
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

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
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



