package com.example.green_agriculture.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.green_agriculture.toolkit.LogUtils

class ModalWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    init {

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        LogUtils.d("================onFinishInflate")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LogUtils.d("================onAttachedToWindow")
    }
}