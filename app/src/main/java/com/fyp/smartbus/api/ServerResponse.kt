package com.fyp.smartbus.api

data class UserResponse(
        var status: Int,
        var error: String? = null,
        var response: User? = null
)

data class BusResponse(
        var status: Int,
        var error: String? = null,
        var response: Bus? = null
)

data class BusOnlineResponse(
        var status: Int,
        var error: String? = null,
        var response: BusOnline? = null
)