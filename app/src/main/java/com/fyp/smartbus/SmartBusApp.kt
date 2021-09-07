package com.fyp.smartbus

import android.app.Application
import com.fyp.smartbus.utils.KEY_IS_DRIVING
import com.fyp.smartbus.utils.sharedPref

class SmartBusApp: Application() {

    var isDrivingServiceRunning = false

    override fun onCreate() {
        super.onCreate()

        isDrivingServiceRunning = sharedPref.getBoolean(KEY_IS_DRIVING, false)
    }

}