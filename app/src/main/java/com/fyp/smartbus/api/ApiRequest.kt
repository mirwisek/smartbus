package com.fyp.smartbus.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiRequest {

    @POST("/api/users/createuser")
    fun createUser(@Body user: RequestBody): Call<UserResponse>

    @PATCH("users")
    fun updateUser(@Body user: User): Call<StringResponse>

    @POST("/api/users/login")
    fun loginUser(@Body user: RequestBody) : Call<UserResponse>

}