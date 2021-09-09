package com.fyp.smartbus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fyp.smartbus.MainActivity
import com.fyp.smartbus.R
import com.fyp.smartbus.login.viewmodel.BusListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var vmBusList: BusListViewModel

    private lateinit var mMap: GoogleMap
    private lateinit var mainActivity: MainActivity

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
    }

    fun onLocationReceived(loc: LatLng) {
        val position = CameraPosition.builder()
            .target(loc)
            .zoom(20f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }
}