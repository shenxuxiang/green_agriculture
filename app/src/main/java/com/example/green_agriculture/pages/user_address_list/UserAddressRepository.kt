package com.example.green_agriculture.pages.user_address_list

import com.example.green_agriculture.api.UserAddressServiceApi
import com.example.green_agriculture.entity.PaginationData
import com.example.green_agriculture.entity.UserAddressItemOption
import com.example.green_agriculture.http.await
import com.example.green_agriculture.toolkit.LogUtils
import com.google.gson.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAddressRepository @Inject constructor(private val serviceApi: UserAddressServiceApi) {
    suspend fun queryUserAddressList(requestBody: JsonObject): PaginationData<List<UserAddressItemOption>> {
        val resp = serviceApi.queryUserAddressList(requestBody).await()

        return resp!!.data
    }

    suspend fun queryUserAddressSetDefault(requestBody: JsonObject): Boolean {
        return try {
            val result = serviceApi.queryUserAddressSetDefault(requestBody).await()
            result!!.data
        } catch (t: Throwable) {
            LogUtils.e(t)
            false
        }
    }
}