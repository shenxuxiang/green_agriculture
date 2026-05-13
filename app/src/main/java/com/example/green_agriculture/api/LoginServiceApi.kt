package com.example.green_agriculture.api

import com.example.green_agriculture.entity.ResponseData
import com.example.green_agriculture.entity.UserInformation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginServiceApi {
    @POST("v1.0/auth/sms/send")
    fun sendPhoneCode(@Body requestBody: Map<String, @JvmSuppressWildcards Any>): Call<ResponseData<Boolean>>

    @POST("/v1.0/auth/login/phoneCode")
    fun queryLoginPhoneCode(@Body requestBody: Map<String, @JvmSuppressWildcards Any>): Call<ResponseData<UserInformation>>
}