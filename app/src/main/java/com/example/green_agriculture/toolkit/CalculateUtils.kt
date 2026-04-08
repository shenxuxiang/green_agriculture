package com.example.green_agriculture.toolkit

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import com.example.green_agriculture.GAApplication

object CalculateUtils {
    val navigationBarHeight: Float
        @SuppressLint("ALL")
        get() {
            val resources = GAApplication.context.resources
            val resID = resources.getIdentifier("navigation_bar_height", "dimen", "android")

            return resources.getDimension(resID)
        }


    val statusBarHeight: Float
        @SuppressLint("ALL")
        get() {
            val resources = GAApplication.context.resources
            val resID = resources.getIdentifier("status_bar_height", "dimen", "android")

            return resources.getDimension(resID)
        }

    fun dpToPx(dp: Int, context: Context? = null): Float {
        val ctx = context ?: GAApplication.context
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            ctx.resources.displayMetrics
        )
    }

    fun dpToPx(dp: Float, context: Context? = null): Float {
        val ctx = context ?: GAApplication.context
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            ctx.resources.displayMetrics
        )
    }
}