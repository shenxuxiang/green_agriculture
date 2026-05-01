package com.example.green_agriculture.api

import com.example.green_agriculture.entity.ResponseData
import com.example.green_agriculture.entity.UserCheckStatus
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MineServiceApi {
    @POST("v1.0/sysUser/getCheckStatus")
    fun queryUserCheckStatus(@Body body: Map<String, String> = emptyMap()): Call<ResponseData<UserCheckStatus>>
}
