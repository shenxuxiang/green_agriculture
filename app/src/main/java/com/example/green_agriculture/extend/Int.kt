package com.example.green_agriculture.extend

import android.util.TypedValue
import com.example.green_agriculture.GAApplication

/**
 * 将当前数值（单位 DIP）转换成像素值
 * @return 返回像素值
 */
val Int.dp: Float
    get() =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            GAApplication.context.resources.displayMetrics
        )

val Int.sp: Float
    get() =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            GAApplication.context.resources.displayMetrics
        )
