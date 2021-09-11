package com.fyp.smartbus.api.app

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

        NetworkFactory.service.createUser(requestBody).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val body = response.body()
                if (response.code() == 200) {
//                    log("inside createUser network...${body?.response.toString()}")
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

    fun forgotPass(email: String, onResult: (Result<String>) -> Unit) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM).apply {
                addFormDataPart("email", email)
            }.build()

        NetworkFactory.service.forgotPass(requestBody).enqueue(object : Callback<StringResponse> {
            override fun onResponse(call: Call<StringResponse>, response: Response<StringResponse>) {
                val body = response.body()
                log("inside network login...${response.body()}")
                if (response.code() == 200) {
                    onResult(Result.success(body?.response!!))
                } else if (response.code() == 403) {
                    onResult(Result.failure(Exception("Email Not Sent/Not Found!!")))
                } else {
                    onResult(Result.failure(Exception("Server Error: ${body?.error}")))
                }
            }

            override fun onFailure(call: Call<StringResponse>, t: Throwable) {
                log("[Forgot Pass] On failure called network... ${t.message}")
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

    fun updateBus(bus: Bus, onResult: (Result<String>) -> Unit) {
        val requestBodyUpdate = MultipartBody.Builder()
            .setType(MultipartBody.FORM).apply {
                addFormDataPart("email", bus.email)
                addFormDataPart("isonline", (if(bus.isonline!!) 1 else 0).toString() )
                bus.currentloc?.let { loc ->
                    addFormDataPart("currentloc", loc)
                }
            }.build()

        NetworkFactory.service.updateBus(requestBodyUpdate).enqueue(object : Callback<StringResponse> {
            override fun onResponse(call: Call<StringResponse>, response: Response<StringResponse>) {
                val body = response.body()
                if (response.code() == 200) {
                    onResult(Result.success(body?.response!!))
                } else if (response.code() == 403) {
                    onResult(Result.failure(Exception("Email is incorrect/Not Found!!")))
                } else {
                    onResult(Result.failure(Exception("Server Error...")))
                }
            }

            override fun onFailure(call: Call<StringResponse>, t: Throwable) {
                log("On failure called network... ${t.message}")
                onResult(Result.failure(t))
            }

        })
    }

    fun getAllBuses(onResult: (Result<List<Bus>>) -> Unit) {

        NetworkFactory.service.getAllBuses().enqueue(object : Callback<BusListResponse> {
            override fun onResponse(call: Call<BusListResponse>, response: Response<BusListResponse>) {
                val body = response.body()
                if (response.code() == 200) {
                    // Empty list if null
                    onResult(Result.success(body?.response ?: listOf()))
                } else {
                    onResult(Result.failure(Exception("Server Error...")))
                }
            }

            override fun onFailure(call: Call<BusListResponse>, t: Throwable) {
                log("On failure called network... ${t.message}")
                onResult(Result.failure(t))
            }

        })
    }

    fun getAllUsers(onResult: (Result<List<User>>) -> Unit) {

        NetworkFactory.service.getAllUsers().enqueue(object : Callback<AccountsResponse> {
            override fun onResponse(call: Call<AccountsResponse>, response: Response<AccountsResponse>) {
                val body = response.body()
                if (response.code() == 200) {
                    // Empty list if null
                    onResult(Result.success(body?.response ?: listOf()))
                } else {
                    onResult(Result.failure(Exception("Server Error...")))
                }
            }

            override fun onFailure(call: Call<AccountsResponse>, t: Throwable) {
                log("On failure called network... ${t.message}")
                onResult(Result.failure(t))
            }
        })
    }

    fun deleteUser(email: String, onResult: (Result<String>) -> Unit) {
        val requestBodyDel = MultipartBody.Builder()
            .setType(MultipartBody.FORM).apply {
                addFormDataPart("email", email)
            }.build()

        NetworkFactory.service.deleteUser(requestBodyDel).enqueue(object : Callback<StringResponse> {
            override fun onResponse(call: Call<StringResponse>, response: Response<StringResponse>) {
                val body = response.body()
                log("inside network login...${response.body()}")
                when(response.code()) {
                    200 -> {
                        onResult(Result.success(body?.response!!))
                    }
                    403 -> {
                        onResult(Result.failure(Exception("Email is incorrect/Not Found!!")))
                    }
                    400 -> {
                        onResult(Result.failure(Exception("Couldn't delete the record")))
                    }
                    else -> {
                        onResult(Result.failure(Exception("Server Error: " + body?.error)))
                    }
                }
            }

            override fun onFailure(call: Call<StringResponse>, t: Throwable) {
                log("[Delete User] On failure called network... ${t.message}")
                onResult(Result.failure(t))
            }

        })
    }

}