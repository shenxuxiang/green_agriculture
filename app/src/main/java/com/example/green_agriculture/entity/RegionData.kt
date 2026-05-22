package com.example.green_agriculture.entity

data class RegionData(
    val value: String,
    val label: String,
    val fullName: String,
    val children: List<RegionData>?,
)