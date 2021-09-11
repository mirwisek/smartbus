package com.fyp.smartbus.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fyp.smartbus.MainActivity
import com.fyp.smartbus.R
import com.fyp.smartbus.api.app.Bearings
import com.fyp.smartbus.api.app.Bus
import com.fyp.smartbus.databinding.FragmentHomeBinding
import com.fyp.smartbus.login.viewmodel.BusListViewModel
import com.fyp.smartbus.ui.buses.BusListFragmentDirections
import com.fyp.smartbus.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var vmBusList: BusListViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var mMap: GoogleMap
    private lateinit var mainActivity: MainActivity
    private var polyLine: Polyline? = null

    private var busMarkers: HashMap<String, Marker> = hashMapOf()
    private var bearings: HashMap<String, Bearings> = hashMapOf()

    private val args: HomeFragmentArgs by navArgs()

    companion object {
        const val TAG = "FragmentHomeTag"
        private const val PADDING_CAMERA = 300
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vmBusList =
            ViewModelProvider(requireActivity()).get(BusListViewModel::class.java)
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mainActivity = requireActivity() as MainActivity

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mainActivity.onMapReady(mMap)

        // If it has came from BusListFragment show bus
        args.busLocation?.let { busLoc ->
            val loc = busLoc.toLatLng()
            val position = CameraPosition.builder()
                .target(loc)
                .zoom(20f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
        }

        vmBusList.busList.observe(viewLifecycleOwner) { bList ->
            val b = vmBusList.selectedBus.value
            bList.forEach { bus ->
                var icon = R.drawable.ic_bus_marker_offline
                if (bus.isonline == true) {
                    icon = R.drawable.ic_bus_marker_online
                    // Just animate them
                    if (bus.currentloc != null && bus.lastloc != null) {
                        val lastLoc = bus.lastloc!!.toLatLng()
                        val currentLoc = bus.currentloc!!.toLatLng()

                        val marker = busMarkers.getOrElse(bus.busno!!, { null })
                        // If the marker hasn't been drawn on map yet, add it first
                        if (marker == null) {
                            addStaticMarker(bus, icon)
                        } else {
                            // Otherwise just animate it
                            // Make sure icon online status is set
                            marker.setIcon(BitmapDescriptorFactory.fromResource(icon))
                            marker.animateMarker(bus, lastLoc, currentLoc)
                        }
                    }

                    log("The value of b is $b")
                    // If the selected bus location has updated then update the ETA
                    if(b != null && b.busno == bus.busno && b.currentloc != bus.currentloc) {
                        log("Updating ETA")
                        vmBusList.getDirections(mainActivity.currentLocation!!)
                    }
                } else {
                    // Just draw them
                    addStaticMarker(bus, icon)
                }
            }
        }

        // Show directions
        vmBusList.busDirections.observe(viewLifecycleOwner) { route ->
            route.overviewPolyLine?.points?.let { points ->
                val poly = PolyUtil.decode(points)
                polyLine = mMap.addPolyline(
                    PolylineOptions()
                        .color(requireContext().getMyColor(R.color.light_blue_600))
                        .width(22f)
                        .addAll(poly)
                )

                mainActivity.addCurrentLocationMarker()

                // Set camera zoom level to fit the polyline
                val latLngBounds = LatLngBounds.Builder().apply {
                    poly.forEach { p -> include(p) }
                }.build()
                val cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, PADDING_CAMERA)
                mMap.animateCamera(cu)
            }
        }

        // ETA error handling
        vmBusList.busDirError.observe(viewLifecycleOwner) {
            it?.let { e ->
                toast("Error fetching directions: ${e.localizedMessage}")
            }
        }

        vmBusList.calculatedETA.observe(viewLifecycleOwner) { eta ->
            if (eta != null) {
                binding.eta.text = eta.first
                binding.description.text = eta.second
                binding.detailsLayout.visible()

                binding.btnClose.setOnClickListener {
                    binding.detailsLayout.gone()
                    polyLine?.remove()
                    mainActivity.markerCurrentLoc?.remove()
                    polyLine = null
                    mainActivity.markerCurrentLoc = null
                }
            }
        }
    }



    private fun addStaticMarker(bus: Bus, icon: Int) {
        val loc = bus.currentloc ?: bus.lastloc
        loc?.let { location ->
            bus.busno?.also {
                val mOptions = requireContext().getBusMarkerOptions(location.toLatLng(), it, icon)
                busMarkers.getOrElse(it, { null })?.remove()
                busMarkers[it] = mMap.addMarker(mOptions)!!
            }
        }
    }

    private fun Marker.animateMarker(bus: Bus, lastLoc: LatLng, currentLoc: LatLng) {
//        val bearing = bearingBetweenLocations(lastLoc, currentLoc).toFloat()
        val bearing = SphericalUtil.computeHeading(lastLoc, currentLoc).toFloat()
        val b: Bearings
        // TODO: Bearing really needs hardwork
        bus.busno!!.let { busNo ->
            val old = bearings.getOrElse(busNo, { null })
            bearings[busNo] = if (old == null)
                Bearings(bearing, bearing)
            else
                Bearings(old.newBearing, bearing)

            b = bearings[busNo]!!
        }
        val shouldRotate = b.oldBearing != b.newBearing
        log("Bearing is $b and $shouldRotate")
        animateMarkerToGB(
            currentLoc, shouldRotate,
            LatLngInterpolator.Spherical(), bearing, Looper.getMainLooper()
        )
    }
}