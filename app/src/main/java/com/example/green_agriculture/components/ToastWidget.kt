package com.example.green_agriculture.components

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.CalculateUtils

class ToastWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val messageView: TextView
    private val prefixIcon: IconWidget
    val paddingX = CalculateUtils.dpToPx(12, context).toInt()
    val paddingY = CalculateUtils.dpToPx(16, context).toInt()

    val corner = CalculateUtils.dpToPx(8, context)

    var message: String = ""
        set(value) {
            if (value == field) return

            field = value
            messageView.text = message
        }

    var iconType: String = "none"
        set(value) {
            if (value == field) return

            field = value

            when (value) {
                "none" -> prefixIcon.visibility = View.GONE
                "success" -> {
                    prefixIcon.visibility = View.VISIBLE
                    prefixIcon.iconColor = context.getColor(R.color.primary)
                    prefixIcon.iconName = context.getString(R.string.icon_success)
                }

                "warn" -> {
                    prefixIcon.visibility = View.VISIBLE
                    prefixIcon.iconColor = context.getColor(R.color.red)
                    prefixIcon.iconName = context.getString(R.string.icon_warn)
                }

                else -> {}
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.toast_widget, this, true).apply {
            messageView = findViewById(R.id.message)
            prefixIcon = findViewById(R.id.prefix_icon)
            messageView.maxWidth = (context.resources.displayMetrics.widthPixels * 0.65).toInt()
            gravity = Gravity.START or Gravity.CENTER_VERTICAL

            setPadding(paddingX, paddingY, paddingX, paddingY)
            background = GradientDrawable().apply {
                setColor(0xAA000000.toInt())
                cornerRadius = corner
            }
        }
    }

    companion object {
        private const val FRACTION = 0.4f

        fun show(activity: Activity, message: String, iconType: String = "normal"): ToastWidget {
            val toast = ToastWidget(activity as Context)
            val top = (activity.resources.displayMetrics.heightPixels * FRACTION).toInt()

            toast.message = message
            toast.iconType = iconType
            toast.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                setMargins(0, top, 0, 0)
            }

            (activity.window.decorView as ViewGroup).addView(toast)

            val animation = AnimationUtils.loadAnimation(activity, R.anim.toast_widget_enter_anim)
            toast.startAnimation(animation)
            return toast
        }
    }
}