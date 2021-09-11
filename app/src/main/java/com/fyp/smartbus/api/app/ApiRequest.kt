package com.fyp.smartbus.api.app

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

    @GET("/getAccounts")
    fun getAllUsers() : Call<AccountsResponse>

    @POST("/deleteUser")
    fun deleteUser(@Body user: RequestBody) : Call<StringResponse>

}