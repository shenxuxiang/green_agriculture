package com.example.green_agriculture.pages.login

import com.example.green_agriculture.api.LoginServiceApi
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import com.google.gson.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val serviceApi: LoginServiceApi) {
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