package com.example.green_agriculture.pages.home

import com.example.green_agriculture.api.HomeServiceApi
import com.example.green_agriculture.entity.SwiperWidgetItemOption
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val service: HomeServiceApi) {
    suspend fun queryBannerList(): List<SwiperWidgetItemOption> {
        return try {
            val resp = service.queryBannerList(body = mapOf()).await()
            resp!!.data
        } catch (t: Exception) {
            LogUtils.d(t)
            emptyList()
        }
    }
}