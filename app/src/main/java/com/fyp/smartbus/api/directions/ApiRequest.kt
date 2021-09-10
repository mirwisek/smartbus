package com.fyp.smartbus.api.directions

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequest {

    @GET("/maps/api/directions/json?key=AIzaSyAfsf6IFxoAKQTfxmX0RuBXKtwKrMYt7VM")
    fun getJson(@Query("origin") origin: String,
                @Query("destination") dest: String,
                @Query("mode") mode: String): Call<DirectionResult>


}