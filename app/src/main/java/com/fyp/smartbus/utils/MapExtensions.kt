package com.fyp.smartbus.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


fun Context.getBusMarkerOptions(location: LatLng, title: String, @DrawableRes drawable: Int): MarkerOptions {
    return MarkerOptions()
        .icon(BitmapDescriptorFactory.fromResource(drawable))
        .position(LatLng(location.latitude, location.longitude))
        .anchor(0.5F, 0.5F)
        .flat(true)
        .title(title)
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

// it Calculates better than Google's bearingTo method (Which returns -ve values sometime)
fun bearingBetweenLocations(prevLoc: LatLng, newLoc: LatLng): Double {
    val PI = 3.14159
    val lat1 = prevLoc.latitude * PI / 180
    val long1 = prevLoc.longitude * PI / 180
    val lat2 = newLoc.latitude * PI / 180
    val long2 = newLoc.longitude * PI / 180
    val dLon = long2 - long1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - (sin(lat1)
            * cos(lat2) * cos(dLon))
    var brng = atan2(y, x)
    brng = Math.toDegrees(brng)
    brng = (brng + 360) % 360
    return brng
}

// Marker Translation

fun Marker.animateMarkerToGB(
    finalPosition: LatLng, shouldRotate: Boolean,
    latLngInterpolator: LatLngInterpolator, toRotation: Float, looper: Looper,
    markerUpdateCallback: ((marker: Marker) -> Unit)? = null
) {
    val marker = this
    val startPosition = marker.position
    val handler = Handler(looper)
    val timeStart = SystemClock.uptimeMillis()
    val interpolator = AccelerateDecelerateInterpolator()
    val rotInterpolator: Interpolator = LinearInterpolator()
    val durationInMs = 2000f
    val rotDuration: Long = 1555
    handler.post(object : Runnable {
        var elapsed: Long = 0
        var t: Float = 0.toFloat()
        var v: Float = 0.toFloat()

        override fun run() {
            // Calculate progress using interpolator
            elapsed = SystemClock.uptimeMillis() - timeStart
            if (shouldRotate) {
                val rotT: Float = rotInterpolator.getInterpolation(elapsed.toFloat() / rotDuration)
                val rot = rotT * toRotation + (1 - rotT)
                marker.rotation = if (-rot > 180) rot / 2 else rot
            }

            t = elapsed / durationInMs
            v = interpolator.getInterpolation(t)
            marker.position = latLngInterpolator.interpolate(v, startPosition, finalPosition)
            markerUpdateCallback?.invoke(marker)
            // Repeat till progress is complete.
            if (t < 1) {
                // Post again 16ms later.
                handler.postDelayed(this, 16)
            }
        }
    })
}

fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}