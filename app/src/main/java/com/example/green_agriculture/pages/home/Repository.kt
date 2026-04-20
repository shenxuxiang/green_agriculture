package com.example.green_agriculture.pages.home

import com.example.green_agriculture.api.HomeServiceApi
import com.example.green_agriculture.http.HttpRequest
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor() {
    private val service = HttpRequest.create<HomeServiceApi>()

    suspend fun queryBannerList(): List<String> {
        return try {
            val resp = service.queryBannerList(body = mapOf()).await()
            val data = resp!!.data as List<Map<*, *>>
            data.map { it["imageUrl"] as String }
        } catch (t: Exception) {
            LogUtils.d(t)
            emptyList()
        }
    }
}