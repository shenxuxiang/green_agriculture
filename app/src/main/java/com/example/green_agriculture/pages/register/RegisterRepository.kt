package com.example.green_agriculture.pages.register

import com.example.green_agriculture.api.RegisterServiceApi
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import com.google.gson.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterRepository @Inject constructor(private val serviceApi: RegisterServiceApi) {
    /**
     * 发送注册验证码
     */
    suspend fun sendPhoneCode(requestBody: JsonObject): Boolean {
        return try {
            val resp = serviceApi.sendPhoneCode(requestBody).await()
            resp!!.data
        } catch (t: Throwable) {
            LogUtils.d(t)
            false
        }
    }

    /**
     * 注册
     */
    suspend fun register(requestBody: JsonObject): Boolean {
        return try {
            serviceApi.register(requestBody).await()
            true
        } catch (t: Throwable) {
            LogUtils.d(t)
            false
        }
    }
}