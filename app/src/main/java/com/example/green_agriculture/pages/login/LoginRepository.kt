package com.example.green_agriculture.pages.login

import com.example.green_agriculture.api.LoginServiceApi
import com.example.green_agriculture.entity.UserInformation
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

    suspend fun queryLoginPhoneCode(requestBody: JsonObject): UserInformation? {
        return try {
            val resp = serviceApi.queryLoginPhoneCode(requestBody).await()

            resp!!.data
        } catch (t: Throwable) {
            LogUtils.d(t)
            return null
        }
    }

    suspend fun queryLoginPassword(requestBody: JsonObject): UserInformation? {
        return try {
            val resp = serviceApi.queryLoginPassword(requestBody).await()

            resp!!.data
        } catch (t: Throwable) {
            LogUtils.d(t)
            return null
        }
    }
}