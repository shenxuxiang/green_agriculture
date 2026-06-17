package com.example.green_agriculture.entity

data class UserAddressItemOption(
    val sort: Int,
    val userId: Long,
    val phone: String,
    val address: String,
    val addressId: Long,
    val username: String,
    val regionCode: String,
    val regionName: String,
    val defaultFlag: Boolean,
)