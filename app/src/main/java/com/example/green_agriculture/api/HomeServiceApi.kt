package com.example.green_agriculture.api

import com.example.green_agriculture.entity.PaginationData
import com.example.green_agriculture.entity.PolicyInformationItemOption
import com.example.green_agriculture.entity.ResponseData
import com.example.green_agriculture.entity.SwiperWidgetItemOption
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HomeServiceApi {
    @POST("v1.0/banner/list")
    fun queryBannerList(@Body body: Map<String, @JvmSuppressWildcards Any>):
            Call<ResponseData<List<SwiperWidgetItemOption>>>

    @POST("v1.0/policyInformation/page")
    fun queryPolicyInformationList(@Body body: Map<String, @JvmSuppressWildcards Any>):
            Call<ResponseData<PaginationData<List<PolicyInformationItemOption>>>>
}