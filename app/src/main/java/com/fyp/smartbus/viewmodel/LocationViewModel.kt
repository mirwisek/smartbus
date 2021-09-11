package com.fyp.smartbus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fyp.smartbus.api.app.Bus
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline

class LocationViewModel : ViewModel() {

    private lateinit var route: ArrayList<Polyline>
    private val location: MutableLiveData<LatLng> by lazy { MutableLiveData<LatLng>() }
    private val pickupDistFromSource: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }

    // A flag to notify us if all buses distances have been checked in a separate thread
    // So that we can finally show result to user
    private val busesCount: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    private val busList: MutableLiveData<ArrayList<Bus>> by lazy {
        MutableLiveData<ArrayList<Bus>>().apply {
            postValue(arrayListOf())
        }
    }


    //region Getters/Setters
    fun getLocation(): LiveData<LatLng> {
        return location
    }

    fun setLocation(loc: LatLng) {
        location.postValue(loc)
    }

    fun getRoute(): ArrayList<Polyline> {
        return route
    }

    fun initRoute(route: ArrayList<Polyline>) {
        this.route = route
    }

    fun setPickupDistFromSource(dist: Int) {
        pickupDistFromSource.postValue(dist)
    }

    fun getPickupDistFromSource(): LiveData<Int> {
        return pickupDistFromSource
    }

    fun getBusList(): LiveData<ArrayList<Bus>> {
        return busList
    }

    fun addBus(bus: Bus) {
        busList.value?.let {
            it.add(bus)
            busList.postValue(it) // Refresh
        }
    }

    fun getBusesCount(): LiveData<Int> {
        return busesCount
    }

    fun setBusesCount(count: Int?) {
        busesCount.postValue(count)
    }

    fun minusBusesCount() {
        busesCount.postValue(busesCount.value!! - 1)
    }

    //endregion

}