package com.example.green_agriculture.toolkit

import com.example.green_agriculture.BuildConfig

object BuilderEnv {
    val isDEV = BuildConfig.BUILD_TYPE == "debug"
    val isPROD = BuildConfig.BUILD_TYPE == "release"
}