package com.fyp.smartbus.api.app

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkFactory {

    private const val TIMEOUT = 70L

    private const val URL_BASE = "http://192.168.10.8:3006/"

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
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

//    private val gson = GsonBuilder()
//            .registerTypeAdapter(Number::class.java, BooleanTypeAdapter())
//            .create()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(okhttp)
        // Scalar converter factory converts String from application/json to text/plain
        // and the we don't get double quotes at server side
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val service: ApiRequest = retrofit.create(ApiRequest::class.java)

}
