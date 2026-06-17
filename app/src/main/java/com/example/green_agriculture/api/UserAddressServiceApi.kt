package com.example.green_agriculture.api

import com.example.green_agriculture.entity.PaginationData
import com.example.green_agriculture.entity.ResponseData
import com.example.green_agriculture.entity.UserAddressItemOption
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAddressServiceApi {
    @POST("v1.0/userAddress/page")
    fun queryUserAddressList(@Body requestBody: JsonObject):
            Call<ResponseData<PaginationData<List<UserAddressItemOption>>>>

    @POST("v1.0/userAddress/setDefault")
    fun queryUserAddressSetDefault(@Body requestBody: JsonObject): Call<ResponseData<Boolean>>
}