package com.example.green_agriculture.pages.login

import com.example.green_agriculture.api.LoginServiceApi
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val serviceApi: LoginServiceApi) {
    suspend fun sendPhoneCode(requestBody: Map<String, @JvmSuppressWildcards Any>): Boolean {
        return try {
            val resp = serviceApi.sendPhoneCode(requestBody).await()
            resp!!.data
        } catch (t: Throwable) {
            LogUtils.d(t)
            return false
        }
    }

}