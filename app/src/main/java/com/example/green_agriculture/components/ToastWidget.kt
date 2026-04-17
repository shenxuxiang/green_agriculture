package com.example.green_agriculture.components

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
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

    /**
     * paddingX - Toast 水平方向的内边距
     * paddingY - Toast 垂直方向的内边距
     * corner   - Toast 圆角
     * message  - Toast 提示文本
     * icon     - Toast icon类型，目前只有 success、warn 两种类型，其他一概不展示
     */
    val paddingX = CalculateUtils.dpToPx(12, context).toInt()
    val paddingY = CalculateUtils.dpToPx(16, context).toInt()
    val corner = CalculateUtils.dpToPx(8, context)
    var message: String = ""
        set(value) {
            if (value == field) return

            field = value
            messageView.text = message
        }

    var icon: String = ""
        set(value) {
            if (value == field) return

            field = value

            when (value) {
                "success" -> {
                    prefixIcon.visibility = VISIBLE
                    prefixIcon.iconColor = context.getColor(R.color.primary)
                    prefixIcon.iconName = context.getString(R.string.icon_success)
                }

                "warn" -> {
                    prefixIcon.visibility = VISIBLE
                    prefixIcon.iconColor = context.getColor(R.color.red)
                    prefixIcon.iconName = context.getString(R.string.icon_warn)
                }

                else -> prefixIcon.visibility = GONE
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.toast_widget, this, true).apply {
            messageView = findViewById(R.id.message)
            prefixIcon = findViewById(R.id.prefix_icon)

            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            setPadding(paddingX, paddingY, paddingX, paddingY)

            // Toast 提示文本的最大宽度不应该超出屏幕宽度的 65%
            messageView.maxWidth = (context.resources.displayMetrics.widthPixels * 0.65).toInt()

            background = GradientDrawable().apply {
                setColor(0xAA000000.toInt())
                cornerRadius = corner
            }
        }
    }

    companion object {
        private const val FRACTION = 0.4f

        /**
         * 展示 Toast
         */
        fun show(activity: Activity, message: String, icon: String = "normal"): ToastWidget {
            val toast = ToastWidget(activity as Context)
            val top = (activity.resources.displayMetrics.heightPixels * FRACTION).toInt()

            // 设置 Toast 的 icon、提示文本
            toast.icon = icon
            toast.message = message

            // 设置 Toast 在容器中的尺寸、位置信息
            toast.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                setMargins(0, top, 0, 0)
            }

            (activity.window.decorView as ViewGroup).addView(toast)

            // 添加入场动画
            val animation = AnimationUtils.loadAnimation(activity, R.anim.toast_widget_enter_anim)
            toast.startAnimation(animation)
            return toast
        }

        /**
         * 隐藏 Toast
         */
        fun hide(activity: Activity, toast: ToastWidget) {
            if (activity.isDestroyed) return

            // 添加出场动画
            val animation = AnimationUtils.loadAnimation(activity, R.anim.toast_widget_exit_anim)

            // 设置动画监听，在出场动画完成时，立即卸载。
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    (activity.window.decorView as ViewGroup).removeView(toast)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })

            toast.startAnimation(animation)
        }
    }
}