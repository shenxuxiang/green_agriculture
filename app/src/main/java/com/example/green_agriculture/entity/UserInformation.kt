package com.example.green_agriculture.entity

data class UserInformation(
    val sex: String,
    val status: Int, // 状态
    val phone: String,
    val token: String,
    val userId: String, // 用户id
    val avatar: String, // 头像
    val address: String, // 详细地址
    val username: String, // 用户名
    val checked: Boolean, // 是否已审核
    val idCardNum: String, // 身份证号码
    val profession: String, // 职位
    val regionName: String, // 所在地区名称
    val regionCode: String, // 所在地区 code
    val description: String, // 描述
    val idCardValidTime: String, // 身份证有效期
)