package com.example.green_agriculture.pages.home

import com.example.green_agriculture.api.HomeServiceApi
import com.example.green_agriculture.entity.PolicyInformationItemOption
import com.example.green_agriculture.entity.SwiperWidgetItemOption
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import com.google.gson.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(private val service: HomeServiceApi) {
    // 获取轮播图列表
    suspend fun queryBannerList(): List<SwiperWidgetItemOption>? {
        return try {
            val resp = service.queryBannerList(body = JsonObject()).await()
            resp!!.data
        } catch (t: Exception) {
            LogUtils.d(t)
            null
        }
    }

    // 获取农业咨询列表
    suspend fun queryPolicyInformationList(): List<PolicyInformationItemOption>? {
        return try {
            val json = JsonObject().apply {
                addProperty("pageSize", 5)
                addProperty("pageNum", 1)
            }
            val resp = service.queryPolicyInformationList(json).await()
            resp!!.data.list
        } catch (t: Exception) {
            LogUtils.d(t)
            null
        }
    }
}