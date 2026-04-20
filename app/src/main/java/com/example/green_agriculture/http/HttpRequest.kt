package com.example.green_agriculture.http

import com.example.green_agriculture.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpRequest {
    private const val TIMEOUT = 5L
    private const val BASE_URL = BuildConfig.BASE_URL
    val client = HttpClient.create(HttpInterceptor(), TIMEOUT)
    private val retrofit = Retrofit.Builder().run {
        client(client)
        baseUrl(BASE_URL)
        addConverterFactory(GsonConverterFactory.create())
        build()
    }

    fun <T> create(serviceClass: Class<T>): T {
        return retrofit.create<T>(serviceClass)
    }

    inline fun <reified T> create() = create(T::class.java)
}