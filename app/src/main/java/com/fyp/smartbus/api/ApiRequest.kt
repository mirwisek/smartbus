package com.fyp.smartbus.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiRequest {

    @POST("/createuser")
    fun createUser(@Body user: RequestBody): Call<UserResponse>

    @PATCH("/updatebus")
    fun updateBus(@Body user: RequestBody): Call<BusResponse>

    @POST("/login")
    fun loginUser(@Body user: RequestBody) : Call<UserResponse>

    @GET("/getbusdetail")
    fun getOnlineBus(@Body bus : RequestBody) : Call<BusOnlineResponse>

}