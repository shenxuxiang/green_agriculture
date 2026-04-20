package com.example.green_agriculture.toolkit

import com.example.green_agriculture.BuildConfig

object AppEnv {
    const val isDEV = BuildConfig.BUILD_TYPE == "debug"
    const val isPROD = BuildConfig.BUILD_TYPE == "release"
    const val baseURL = BuildConfig.BASE_URL
}