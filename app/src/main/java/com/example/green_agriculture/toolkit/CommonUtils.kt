package com.example.green_agriculture.toolkit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

object CommonUtils {
    /**
     * 获取当前 Activity
     */
    fun getActivity(context: Context): Activity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }

        return null
    }

    fun networkImageUrl(url: String): String {
        if (Regex("""^https?://""").matches(url)) return url

        return if (url.startsWith("/")) {
            "${AppEnv.baseURL}$url"
        } else {
            "${AppEnv.baseURL}/$url"
        }
    }
}