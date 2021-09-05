package com.fyp.smartbus.api

import com.fyp.smartbus.utils.log
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiHelper {

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
        NetworkFactory.service.createUser(requestBody).enqueue(object : Callback<UserResponse> {
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
        log("Server network...${NetworkFactory.service.toString()}")
        NetworkFactory.service.loginUser(requestBody).enqueue(object : Callback<UserResponse> {
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
//                log("isonline: ${bus.isonline}")
                // TODO: [Zain - Fix boolean value send to retrofit]
                addFormDataPart("isonline", (if (bus.isonline!!) 1 else 0).toString())
                addFormDataPart("currentloc", bus.currentloc!!)
            }.build()

//        log("inside network signup...${requestBody.toString()}")
        NetworkFactory.service.updateBus(requestBodyUpdate).enqueue(object : Callback<BusResponse> {
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


    fun getBusOnline(onResult: (Result<BusOnline>) -> Unit) {
        val requestBody = MultipartBody.Builder()
            .build()
        log("Server network...${NetworkFactory.service.toString()}")
        NetworkFactory.service.getOnlineBus(requestBody)
            .enqueue(object : Callback<BusOnlineResponse> {
                override fun onResponse(
                    call: Call<BusOnlineResponse>,
                    response: Response<BusOnlineResponse>
                ) {
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

                override fun onFailure(call: Call<BusOnlineResponse>, t: Throwable) {
                    log("On failure called network... ${t.message}")
                    onResult(Result.failure(t))
                }

            })
    }

}