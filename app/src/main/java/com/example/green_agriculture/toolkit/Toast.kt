package com.example.green_agriculture.toolkit

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.green_agriculture.components.ToastWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Toast {
    private lateinit var activity: Activity

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val channel = Channel<String>(Channel.UNLIMITED, BufferOverflow.SUSPEND)

    fun initialize(activity: Activity) {
        this.activity = activity
    }

    fun show(message: String) {
        LogUtils.d("================== $message")
        coroutineScope.launch {
            channel.send("message")
        }
    }

    private fun addToast(): ToastWidget {
        val toastWidget = ToastWidget(activity as Context)
        val top = (activity.resources.displayMetrics.heightPixels * 0.4).toInt()

        toastWidget.title = "系统异常，请联系管理员"
        toastWidget.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            setMargins(0, top, 0, 0)
        }

        (activity.window.decorView as ViewGroup).addView(toastWidget)

        return toastWidget
    }

    private fun removeToast(view: ToastWidget) {
        (activity.window.decorView as ViewGroup).removeView(view)
    }

    init {
        coroutineScope.launch {
            for (msg in channel) {
                val result = channel.tryReceive().getOrNull() ?: msg
                val view = async(Dispatchers.Main) {
                    addToast()
                }.await()

                delay(2000)

                withContext(Dispatchers.Main) { removeToast(view) }
                delay(500)
            }
        }
    }


}