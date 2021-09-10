package com.fyp.smartbus.api.app

import java.util.*


//enum class AccountType {
//    STUDENT, DRIVER
//}

data class User (
        var email: String,
        var password: String="",
        var username: String?=null,
        var busno: String? = null,
        var usertype: String?=null
)
data class Bus(
        var email: String,
        var username: String?=null,
        var isonline : Boolean? = null,
        var busno: String? = null,
        var lastloc : String? = null,
        var currentloc : String? = null
)

data class Bearings(
        var oldBearing: Float,
        var newBearing: Float
)

