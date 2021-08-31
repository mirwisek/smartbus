package com.fyp.smartbus.utils

import android.location.Location
import com.fyp.smartbus.R
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

val QUETTA = LatLng(30.203211, 67.005599)

object MapsUtils {

    fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 5_000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun getDriverMarkerOptions(location: LatLng): MarkerOptions {
        val options = MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus))
            .position(LatLng(location.latitude, location.longitude))
        options.anchor(0.5F, 0.5F)
        options.flat(true)
        return options
    }

    // it Calculates better than Google's bearingTo method (Which returns -ve values sometime)
    fun bearingBetweenLocations(prevLoc: Location, newLoc: Location): Double {
        val PI = 3.14159
        val lat1 = prevLoc.latitude * PI / 180
        val long1 = prevLoc.longitude * PI / 180
        val lat2 = newLoc.latitude * PI / 180
        val long2 = newLoc.longitude * PI / 180
        val dLon = long2 - long1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon))
        var brng = Math.atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        return brng
    }

}