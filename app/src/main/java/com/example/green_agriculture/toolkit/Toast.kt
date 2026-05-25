package com.example.green_agriculture.toolkit

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val channel = Channel<ToastEvent>(10, BufferOverflow.SUSPEND)

    fun initialize(activity: AppCompatActivity) {
        activity.apply {
            // 协程作用域和 Activity 的生命周期绑定在一起，在 Activity 销毁时此协程作用域会自动销毁。
            lifecycleScope.launch(Dispatchers.Default) {
                for (result in channel) {
                    val toast = async(Dispatchers.Main) {
                        ToastWidget.show(
                            icon = result.type,
                            message = result.message,
                            rootView = result.rootView ?: (activity.window.decorView as ViewGroup),
                        )
                    }.await()

                    delay(result.duration)

                    withContext(Dispatchers.Main) {
                        val rootView = result.rootView ?: (activity.window.decorView as ViewGroup)
                        ToastWidget.hide(rootView, toast)
                    }
                    delay(500)
                }
            }
        }
    }

    /**
     * 展示 Toast
     * @param message  提示文本
     * @param duration Toast 展示的时间
     * @param type     icon 类型
     */
    fun show(
        message: String,
        duration: Long = 2000,
        type: String = "none",
        rootView: ViewGroup? = null,
    ) {
        coroutineScope.launch {
            channel.send(ToastEvent(message, duration, type, rootView))
        }
    }

    fun showSuccess(message: String, duration: Long = 2000, rootView: ViewGroup? = null) {
        show(message, duration, "success", rootView)
    }

    fun showWarn(message: String, duration: Long = 2000, rootView: ViewGroup? = null) {
        show(message, duration, "warn", rootView)
    }
}

private data class ToastEvent(
    val message: String,
    val duration: Long,
    val type: String,
    val rootView: ViewGroup? = null,
)