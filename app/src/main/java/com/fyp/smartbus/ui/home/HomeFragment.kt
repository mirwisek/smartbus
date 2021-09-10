package com.fyp.smartbus.ui.home

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fyp.smartbus.MainActivity
import com.fyp.smartbus.R
import com.fyp.smartbus.api.Bearings
import com.fyp.smartbus.api.Bus
import com.fyp.smartbus.login.viewmodel.BusListViewModel
import com.fyp.smartbus.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var vmBusList: BusListViewModel

    private lateinit var mMap: GoogleMap
    private lateinit var mainActivity: MainActivity

    private var busMarkers: HashMap<String, Marker> = hashMapOf()
    private var bearings: HashMap<String, Bearings> = hashMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vmBusList =
            ViewModelProvider(requireActivity()).get(BusListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mainActivity = requireActivity() as MainActivity

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mainActivity.onMapReady()

        vmBusList.busList.observe(viewLifecycleOwner) { bList ->
            bList.forEach { bus ->
                var icon = R.drawable.ic_bus_marker_offline
                if(bus.isonline == true) {
                    icon = R.drawable.ic_bus_marker_online
                    // Just animate them
                    if(bus.currentloc != null && bus.lastloc != null) {
                        val lastLoc = bus.lastloc!!.toLatLng()
                        val currentLoc = bus.currentloc!!.toLatLng()

                        val marker = busMarkers.getOrElse(bus.busno!!, { null })
                        // If the marker hasn't been drawn on map yet, add it first
                        if(marker == null) {
                            addStaticMarker(bus, icon)
                        } else {
                            // Otherwise just animate it
                            marker.animateMarker(bus, lastLoc, currentLoc)
                        }
                    }
                } else {
                    // Just draw them
                    addStaticMarker(bus, icon)
                }
            }
        }
    }

    private fun addStaticMarker(bus: Bus, icon: Int) {
        val loc = bus.currentloc ?: bus.lastloc
        loc?.let { location ->
            val mOptions = requireContext().getBusMarkerOptions(location.toLatLng(), icon)
            busMarkers[bus.busno!!] = mMap.addMarker(mOptions)!!
        }
    }

    private fun Marker.animateMarker(bus: Bus, lastLoc: LatLng, currentLoc: LatLng) {
//        val bearing = bearingBetweenLocations(lastLoc, currentLoc).toFloat()
        val bearing = SphericalUtil.computeHeading(lastLoc, currentLoc).toFloat()
        val b: Bearings
        // TODO: Bearing really needs hardwork
        bus.busno!!.let {  busNo ->
            val old = bearings.getOrElse(busNo, {null})
            bearings[busNo] = if(old == null)
                Bearings(bearing, bearing)
            else
                Bearings(old.newBearing, bearing)

            b = bearings[busNo]!!
        }
        val shouldRotate = b.oldBearing != b.newBearing
        log("Bearing is $b and $shouldRotate")
        animateMarkerToGB(currentLoc, shouldRotate,
            LatLngInterpolator.Spherical(), bearing, Looper.getMainLooper())
    }

    fun onLocationReceived(loc: LatLng) {
        val position = CameraPosition.builder()
            .target(loc)
            .zoom(20f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }
}