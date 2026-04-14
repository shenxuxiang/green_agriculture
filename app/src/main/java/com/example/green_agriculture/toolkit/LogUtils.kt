package com.example.green_agriculture.toolkit

import android.util.Log
import com.example.green_agriculture.BuildConfig

object LogUtils {
    private const val isDev = BuildConfig.BUILD_TYPE == "debug"
    private const val TAG = "GA_APP"

    fun d(msg: String) {
        if (isDev) Log.d(TAG, msg)
    }

    fun d(t: Throwable) {
        if (isDev) Log.d(TAG, t.stackTraceToString())
    }
}