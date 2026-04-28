package com.example.green_agriculture.entity

import com.google.gson.annotations.SerializedName

data class PolicyInformationItemOption(
    @SerializedName("policyInformationId")
    val id: String,
    @SerializedName("primaryUrl")
    val url: String,
    val title: String,
    val updateTime: String,
)
