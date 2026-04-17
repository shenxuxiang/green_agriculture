package com.example.green_agriculture.toolkit

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
                    val view = async(Dispatchers.Main) {
                        ToastWidget.show(activity, result.message, result.type)
                    }.await()

                    delay(result.duration)

                    withContext(Dispatchers.Main) {
                        ToastWidget.hide(activity, view)
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
    fun show(message: String, duration: Long = 2000, type: String = "none") {
        coroutineScope.launch {
            channel.send(ToastEvent(message, duration, type))
        }
    }

    fun showSuccess(message: String, duration: Long = 2000) {
        show(message, duration, "success")
    }

    fun showWarn(message: String, duration: Long = 2000) {
        show(message, duration, "warn")
    }
}

private data class ToastEvent(val message: String, val duration: Long, val type: String)