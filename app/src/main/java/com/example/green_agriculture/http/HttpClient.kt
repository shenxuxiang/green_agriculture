package com.example.green_agriculture.http

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpClient {
    fun create(interceptor: HttpInterceptor, timeout: Long): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .build()
    }
}