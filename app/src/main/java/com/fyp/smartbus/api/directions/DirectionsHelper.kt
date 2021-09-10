package com.fyp.smartbus.api.directions

import android.util.Log
import com.fyp.smartbus.utils.log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    fun fetchResults(
        origin: String,
        dest: String,
        mode: String = "driving",
        onResult: (Result<DirectionResult>) -> Unit
    ) {

        val cb = object : Callback<DirectionResult> {
            override fun onResponse(
                call: Call<DirectionResult>,
                response: Response<DirectionResult>
            ) {
                val body = response.body()
                if (response.code() == 200 && body != null) {
                    onResult(Result.success(body))
                } else {
                    onResult(Result.failure(Exception("Server Error...")))
                }
            }

            override fun onFailure(call: Call<DirectionResult>, t: Throwable) {
                log("On failure called network... ${t.message}")
                onResult(Result.failure(t))
            }

        }

        service.getJson(origin, dest, mode).enqueue(cb)
    }

}
