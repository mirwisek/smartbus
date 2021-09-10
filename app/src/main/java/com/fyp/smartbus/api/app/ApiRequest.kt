package com.fyp.smartbus.api.app

import com.fyp.smartbus.api.directions.DirectionResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiRequest {

    @POST("/createuser")
    fun createUser(@Body user: RequestBody): Call<UserResponse>

    @PATCH("/updatebus")
    fun updateBus(@Body user: MultipartBody): Call<StringResponse>

    @POST("/login")
    fun loginUser(@Body user: RequestBody) : Call<UserResponse>

    @GET("/getBuses")
    fun getAllBuses() : Call<BusListResponse>

    @POST("/forgot-password")
    fun forgotPass(@Body user: RequestBody) : Call<StringResponse>

    @GET("/maps/api/directions/json?key=AIzaSyAfsf6IFxoAKQTfxmX0RuBXKtwKrMYt7VM")
    fun getJson(@Query("origin") origin: String,
                @Query("destination") dest: String,
                @Query("mode") mode: String): Call<DirectionResult>

}