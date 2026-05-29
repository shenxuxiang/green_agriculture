package com.example.green_agriculture.api

import com.example.green_agriculture.entity.ResponseData
import com.example.green_agriculture.entity.UserInformation
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginServiceApi {
    @POST("v1.0/auth/sms/send")
    fun sendPhoneCode(@Body requestBody: JsonObject): Call<ResponseData<Boolean>>

    @POST("v1.0/auth/login/phoneCode")
    fun queryLoginPhoneCode(@Body requestBody: JsonObject): Call<ResponseData<UserInformation>>

    @POST("v1.0/auth/login")
    fun queryLoginPassword(@Body requestBody: JsonObject): Call<ResponseData<UserInformation>>
}