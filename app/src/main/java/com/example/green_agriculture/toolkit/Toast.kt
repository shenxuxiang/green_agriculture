package com.example.green_agriculture.toolkit

import android.app.Activity
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
    private const val FRACTION = 0.4f
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val channel = Channel<ToastEvent>(10, BufferOverflow.SUSPEND)


    fun initialize(activity: AppCompatActivity) {
        activity.apply {
            lifecycleScope.launch(Dispatchers.Default) {
                for (result in channel) {
                    val view = async(Dispatchers.Main) {
                        ToastWidget.show(activity, result.message, result.type)
                    }.await()

                    delay(result.duration)

                    withContext(Dispatchers.Main) { removeToast(view, activity) }
                    delay(500)
                }
            }
        }
    }

    fun show(message: String, duration: Long = 2000, type: String = "none") {
        coroutineScope.launch {
            channel.send(ToastEvent(message, duration, type))
        }
    }

    fun showSuccess(message: String, duration: Long = 2000) {
        show(message, duration, "success")
    }

    fun showSuccessWarn(message: String, duration: Long = 2000) {
        show(message, duration, "warn")
    }

    private fun removeToast(view: ToastWidget, activity: Activity) {
        if (activity.isDestroyed) return

        (activity.window.decorView as ViewGroup).removeView(view)
    }
}

private data class ToastEvent(val message: String, val duration: Long, val type: String)