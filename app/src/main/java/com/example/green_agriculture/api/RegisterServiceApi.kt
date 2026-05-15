package com.example.green_agriculture.api

import com.example.green_agriculture.entity.ResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterServiceApi {
    @POST("v1.0/auth/sms/send")
    fun sendPhoneCode(@Body requestBody: Map<String, @JvmSuppressWildcards Any>): Call<ResponseData<Boolean>>
}