package com.example.green_agriculture.http

import okhttp3.Interceptor
import okhttp3.Response

class HttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val oRequest = chain.request()

        val newRequest = oRequest.newBuilder()
            .header("Authorization", "Bearer ${}")
            .build()

        return chain.proceed(newRequest)
    }

}