package com.fyp.smartbus.api.directions

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DirectionsHelper {

    private const val URL_BASE = "https://maps.googleapis.com/"

    private val logInterceptor = HttpLoggingInterceptor { message ->
        try {
            Log.i("ffnet", message)
        } catch (e: Exception) {
            println("ffnet: $message")
        }
    }.apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val okhttp = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(okhttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val service: ApiRequest = retrofit.create(ApiRequest::class.java)

    fun fetchResults(origin: String, dest: String, callback: Callback<DirectionResult>,
                     mode: String = "driving") {
        service.getJson(origin, dest, mode).enqueue(callback)
    }

}
