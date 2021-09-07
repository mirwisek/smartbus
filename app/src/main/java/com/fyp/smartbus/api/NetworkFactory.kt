package com.fyp.smartbus.api

import android.util.Log
import com.fyp.smartbus.utils.log
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkFactory {

    private const val TIMEOUT = 70L

    private const val URL_BASE = "http://192.168.10.2:3006/"

    private val logInterceptor = HttpLoggingInterceptor { message ->
        try {
            Log.i("ffnet", message)
        } catch (e: Exception) {
            println("ffnet: $message")
        }
    }.apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

//        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

    private val okhttp = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()
//
//        val gson = GsonBuilder()
//                .registerTypeAdapter(LocalDateTime::class.java, JsonToDateTimeConverter())
//                .create()


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
