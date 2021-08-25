package com.fyp.smartbus.api

data class UserResponse(
        var status: Int,
        var error: String? = null,
        var response: User? = null
)

data class StringResponse(
        var status: Int,
        var error: String? = null,
        var response: String? = null
)