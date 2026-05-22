package com.example.green_agriculture.pages.register

import com.example.green_agriculture.api.RegisterServiceApi
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import com.google.gson.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterRepository @Inject constructor(private val serviceApi: RegisterServiceApi) {
    suspend fun sendPhoneCode(requestBody: JsonObject): Boolean {
        return try {
            val resp = serviceApi.sendPhoneCode(requestBody).await()
            resp!!.data
        } catch (t: Throwable) {
            LogUtils.d(t)
            return false
        }
    }
}