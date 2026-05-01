package com.example.green_agriculture.entity

/**
 * 用户审核状态
 */
data class UserCheckStatus(
    val userType: Int,
    val checkStatus: Int,
    val userTypeName: String,
    val checkStatusName: String,
)