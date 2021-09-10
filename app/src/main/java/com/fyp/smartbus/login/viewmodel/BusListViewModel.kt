package com.fyp.smartbus.login.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fyp.smartbus.api.app.ApiHelper
import com.fyp.smartbus.api.app.Bus
import com.fyp.smartbus.api.directions.DirectionResult
import com.fyp.smartbus.api.directions.DirectionsHelper
import com.fyp.smartbus.api.directions.Route
import com.fyp.smartbus.utils.log
import com.fyp.smartbus.utils.string
import com.google.android.gms.maps.model.LatLng

class BusListViewModel(private val app: Application) : AndroidViewModel(app)  {

    val busList = MutableLiveData<List<Bus>>(listOf())
    val error = MutableLiveData<Throwable?>(null)

    val selectedBus = MutableLiveData<Bus>()
    val busDirections = MutableLiveData<Route>()
    val busDirError = MutableLiveData<Throwable?>(null)

    val calculatedETA = MutableLiveData<Pair<String, String>>()

    fun getAllBuses() {
        ApiHelper.getAllBuses { result ->
            result.fold(
                onSuccess = {
                    busList.postValue(it)
                    error.postValue(null)
                },
                onFailure = {
                    busList.postValue(listOf())
                    error.postValue(it)
                }
            )
        }
    }

    fun getDirections(yourLoc: LatLng): Boolean {
        val origin = yourLoc.string()
        val dest: String
        selectedBus.value!!.let {
            dest = (it.currentloc ?: it.lastloc)!!
        }
        DirectionsHelper.fetchResults(origin, dest) { result ->
            result.fold(
                onSuccess = { dirRes ->
                    dirRes.routes?.get(0)?.let { r ->
                        val leg = r.legs?.get(0)
                        val distance = leg?.distance?.text
                        val duration = leg?.duration?.text
                        busDirections.postValue(r)

                        if(distance != null && duration != null) {
                            val title = "$duration ($distance)"
                            val desc = "Bus route from your current location to Bus (${selectedBus.value!!.busno})"
                            calculatedETA.value = Pair(title, desc)
                        }
                    }
                },
                onFailure = {
                    busDirError.postValue(it)
                    log("There is error fetching directions $it")
                }
            )
        }
        return true
    }

}