package com.example.green_agriculture.pages.mine

import com.example.green_agriculture.api.MineServiceApi
import com.example.green_agriculture.entity.UserCheckStatus
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineRepository @Inject constructor(private val serviceApi: MineServiceApi) {
    suspend fun queryUserCheckStatus(): UserCheckStatus? {
        try {
            val resp = serviceApi.queryUserCheckStatus().await()
            return resp?.data
        } catch (t: Throwable) {
            LogUtils.d(t)
            return null
        }
    }
}