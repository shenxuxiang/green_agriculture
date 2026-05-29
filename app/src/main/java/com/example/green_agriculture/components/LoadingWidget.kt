package com.example.green_agriculture.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.toolkit.LogUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingWidget(context: Context) : FrameLayout(context) {
    private val indicator = CircularProgressIndicator(context).apply {
        indicatorInset = 0
        isIndeterminate = true
        trackColor = 0x22FFFFFF
        indicatorSize = indicatorRadius * 2
        trackThickness = indicatorThickness
        trackCornerRadius = indicatorThickness / 2
        setIndicatorColor(context.getColor(R.color.primary))
        layoutParams = LinearLayout.LayoutParams(
            indicatorRadius * 2,
            indicatorRadius * 2,
        )
    }
    private val tips = TextView(context).apply {
        text = "加载中···"
        setTextColor(context.getColor(R.color.white))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        setPadding(10.dp.toInt(), 0, 0, 0)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
    }

    val container = LinearLayout(context).apply {
        gravity = Gravity.CENTER
        orientation = HORIZONTAL

        setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
        background = GradientDrawable().apply {
            cornerRadius = 8.dp
            setColor(0x99000000.toInt())
            shape = GradientDrawable.RECTANGLE

            clipChildren = true
            clipToOutline = true
            clipToPadding = true
        }

        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            val screenH = context.resources.displayMetrics.heightPixels
            val contentH = paddingVertical * 2 + indicatorRadius * 2
            val top = (screenH - contentH) * ALIGN_FRACTION

            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            setMargins(0, top.toInt(), 0, 0)
        }

        addView(indicator)
        addView(tips)
    }

    init {
        setBackgroundColor(context.getColor(android.R.color.transparent))

        setOnClickListener {
            LogUtils.d("==============================Show Loading")
        }
        addView(container)
    }

    enum class WidgetStatus {
        None(), Wait(), Showing()
    }

    companion object {
        private const val DELAY = 200L
        private const val ALIGN_FRACTION = 0.5f
        private val paddingVertical = 12.dp.toInt()
        private val indicatorRadius = 15.dp.toInt()
        private val paddingHorizontal = 18.dp.toInt()
        private val indicatorThickness = 3.dp.toInt()
        private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        /**
         * 插入 LoadingWidget
         */
        private fun onInsertLoadingWidget(rootView: ViewGroup): LoadingWidget {
            val context = rootView.context
            val widget = LoadingWidget(context)
            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
            )

            rootView.addView(widget, layoutParams)

            val animation = AnimationUtils.loadAnimation(context, R.anim.alert_widget_enter_anim)
            widget.container.startAnimation(animation)

            return widget
        }

        /**
         * 移除 LoadingWidget
         */
        private fun onRemoveLoadingWidget(
            rootView: ViewGroup,
            view: LoadingWidget,
            block: (() -> Unit)?,
        ) {
            val context = rootView.context
            val animation = AnimationUtils.loadAnimation(context, R.anim.toast_widget_exit_anim)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    rootView.post {
                        rootView.removeView(view)
                        block?.invoke()
                    }
                }
            })
            view.container.startAnimation(animation)
        }

        /**
         * 展示 Loading
         * @param rootView    插槽位置
         * @return () -> Unit 销毁 Loading 的方法
         */
        fun show(rootView: ViewGroup): (block: (() -> Unit)?) -> Unit {
            var status = WidgetStatus.Wait
            var loadingWidget: LoadingWidget? = null

            val job = coroutineScope.launch {
                delay(DELAY)
                status = WidgetStatus.Showing
                loadingWidget = onInsertLoadingWidget(rootView)
            }

            val onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }

            rootView.findViewTreeOnBackPressedDispatcherOwner()?.let {
                val onBackPressedDispatcher = it.onBackPressedDispatcher
                onBackPressedDispatcher.addCallback(onBackPressedCallback)
            }

            fun callback(block: (() -> Unit)?) {
                when (status) {
                    WidgetStatus.None -> {
                        block?.invoke()
                    }

                    WidgetStatus.Wait -> {
                        job.cancel()
                        block?.invoke()
                    }

                    WidgetStatus.Showing -> {
                        coroutineScope.launch {
                            delay(DELAY)
                            onRemoveLoadingWidget(rootView, loadingWidget!!, block)
                        }
                    }
                }

                onBackPressedCallback.remove()
            }

            return ::callback
        }
    }
}