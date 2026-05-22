package com.example.green_agriculture.toolkit

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

object CommonUtils {
    /**
     * 获取当前 Activity
     */
    fun getActivity(context: Context): AppCompatActivity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is AppCompatActivity) return ctx
            ctx = ctx.baseContext
        }

        return null
    }

    fun networkImageUrl(url: String): String {
        if (Regex("""^https?://""").matches(url)) return url

        return if (url.startsWith("/")) {
            "${AppEnv.BASE_URL}$url"
        } else {
            "${AppEnv.BASE_URL}/$url"
        }
    }
}