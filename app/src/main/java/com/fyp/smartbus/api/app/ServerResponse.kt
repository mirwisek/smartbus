package com.fyp.smartbus.api.app

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

data class StringResponse(
        var status: Int,
        var error: String? = null,
        var response: String? = null
)

data class BusListResponse(
        var status: Int,
        var error: String? = null,
        var response: List<Bus> = listOf()
)