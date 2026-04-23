package com.example.green_agriculture.toolkit

import com.example.green_agriculture.BuildConfig

object AppEnv {
    const val IS_DEV = BuildConfig.BUILD_TYPE == "debug"
    const val IS_PROD = BuildConfig.BUILD_TYPE == "release"
    const val BASE_URL = BuildConfig.BASE_URL
}