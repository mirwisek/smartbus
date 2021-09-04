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

object NetworkFactory {
//    lateinit var service: ApiRequest

    private const val TIMEOUT = 70L

    const val URL_BASE = "http://192.168.10.3:3006/"


    //    init {
//    }
    val logInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            try {
                Log.i("ffnet", message)
            } catch (e: Exception) {
                println("ffnet: $message")
            }
        }

    }).apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

//        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

    val okhttp = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
//        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()
//
//        val gson = GsonBuilder()
//                .registerTypeAdapter(LocalDateTime::class.java, JsonToDateTimeConverter())
//                .create()


    val retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(okhttp)
        // Scalar converter factory converts String from application/json to text/plain
        // and the we don't get double quotes at server side
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val service = retrofit.create(ApiRequest::class.java)

//    log("service network ...$service")
//    fun createUser(user: User, callback: Callback<UserResponse>) {
//        service.createUser(user).enqueue(callback)
//    }
//
//    fun updateUser(user: User, callback: Callback<StringResponse>) {
//        service.updateUser(user).enqueue(callback)
//    }
//    fun LoginUser(user:User, callback: Callback<StringResponse>){
//        service.loginUser(user).enqueue(callback)
//    }

    fun signup(user: User, onResult: (Result<User>) -> Unit) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM).apply {
                addFormDataPart("email", user.email)
                addFormDataPart("password", user.password)
                addFormDataPart("username", user.username!!)
                addFormDataPart("busno", user.busno!!)
                addFormDataPart("usertype", user.usertype!!)
            }.build()

//        log("inside network signup...${requestBody.toString()}")
        service.createUser(requestBody).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val body = response.body()
                if (response.code() == 200) {
//                    log("inside createUser network...${body?.response.toString()}")
//                    Toast.makeText(, "Register Successfully", Toast.LENGTH_SHORT).show()
                    onResult(Result.success(body?.response!!))
//                    onResult(Result.success("Register Successfully."))
                } else if (response.code() == 304) { // On Duplicate, is not error
                    onResult(Result.failure(Exception("User Already Exist...")))
                } else {
                    // body will be null when status code is an error type
//                    val errorBody = Gson().fromJson<UserResponse>(
//                        response.errorBody()?.charStream(), ApiResult::class.java)
                    onResult(Result.failure(Exception("Server Error....")))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }


    fun login(user: User, onResult: (Result<User>) -> Unit) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM).apply {
                addFormDataPart("email", user.email)
                addFormDataPart("password", user.password)
            }.build()
        log("Server network...${service.toString()}")
        service.loginUser(requestBody).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val body = response.body()
                log("inside network login...${response.body()}")
                if (response.code() == 200 && body != null) {
                    onResult(Result.success(body.response!!))
                } else if (response.code() == 403) {
                    onResult(Result.failure(Exception("Email/Password is incorrect!!")))
                } else {
                    onResult(Result.failure(Exception("Server Error...")))
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                log("On failure called network... ${t.message}")
                onResult(Result.failure(t))
            }

        })
    }

    fun updateBus(bus: Bus, onResult: (Result<Bus>) -> Unit) {
        val requestBodyUpdate = MultipartBody.Builder()
            .setType(MultipartBody.FORM).apply {
                addFormDataPart("email", bus.email)
                addFormDataPart("isonline", bus.isonline!!)
                addFormDataPart("currentloc", bus.currentloc!!)
                addFormDataPart("busno", bus.busno!!)
            }.build()

//        log("inside network signup...${requestBody.toString()}")
        service.updateBus(requestBodyUpdate).enqueue(object : Callback<BusResponse> {
            override fun onResponse(call: Call<BusResponse>, response: Response<BusResponse>) {
                val body = response.body()
                log("inside network login...${response.body()}")
                if (response.code() == 200) {
                    onResult(Result.success(body?.response!!))
                } else if (response.code() == 403) {
                    onResult(Result.failure(Exception("Email is incorrect/Not Found!!")))
                } else {
                    onResult(Result.failure(Exception("Server Error...")))
                }
            }

            override fun onFailure(call: Call<BusResponse>, t: Throwable) {
                log("On failure called network... ${t.message}")
                onResult(Result.failure(t))
            }

        })
//                    onResult(Result.success("Register Successfully."))
    }
}