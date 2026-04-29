package com.example.green_agriculture.components

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.toolkit.LogUtils
import com.example.green_agriculture.toolkit.VibratorUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle

class RefreshHeaderWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), RefreshHeader {
    private val headerText: TextView
    private var canRefresh = false
    private val indicator: CircularProgressIndicator

    init {
        headerText = TextView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }

            text = "刷新成功"
            setTextColor(0xFF999999.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
        }

        addView(headerText)
        indicator = CircularProgressIndicator(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    setMargins(0, 0, 0, 10.dp.toInt())
                }
            progress = 0
            indicatorInset = 0
            isIndeterminate = false
            indicatorSize = 20.dp.toInt()
            trackThickness = 2.dp.toInt()
            trackCornerRadius = 1.dp.toInt()
            setIndicatorColor(context.getColor(R.color.primary))

        }
        addView(indicator)
    }

    override fun getView() = this

    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate

    override fun setPrimaryColors(vararg p0: Int) {}

    override fun onInitialized(p0: RefreshKernel, p1: Int, p2: Int) {}

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int,
    ) {
        canRefresh = percent >= 1

        if (percent in 0f..1f) {
            val matrix = Matrix()
            val scale = percent.coerceAtMost(1f)
            matrix.postScale(scale, scale, 10.dp, 10.dp)
            matrix.postRotate(scale * 360f, 10.dp, 10.dp)

            indicator.progress = (scale * 100).toInt()
            indicator.animationMatrix = matrix
        }
    }

    override fun onReleased(
        refreshLayout: RefreshLayout,
        height: Int,
        maxDragHeight: Int,
    ) {
        if (canRefresh) {
            indicator.isIndeterminate = true
        }
    }

    override fun onStartAnimator(
        p0: RefreshLayout,
        p1: Int,
        p2: Int,
    ) {
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        if (success) {
            headerText.text = "刷新成功"
        } else {
            headerText.text = "刷新失败"
        }

        indicator.visibility = GONE
        indicator.isIndeterminate = false

        val translationY = ObjectAnimator.ofFloat(headerText, "translationY", (-50).dp, 0f)
        val alpha = ObjectAnimator.ofFloat(headerText, "alpha", 0f, 1f)

        val animSet = AnimatorSet().apply {
            play(translationY).with(alpha)
            setDuration(300)
        }

        animSet.start()

        return 1000
    }

    override fun onHorizontalDrag(p0: Float, p1: Int, p2: Int) {}

    override fun isSupportHorizontalDrag() = false

    override fun autoOpen(p0: Int, p1: Float, p2: Boolean) = true

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState,
    ) {
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                indicator.visibility = VISIBLE
                headerText.alpha = 0f
            }

            RefreshState.Refreshing -> {}

            RefreshState.ReleaseToRefresh -> {
                LogUtils.d("==================vibrate")
                VibratorUtils.oneShot()
            }

            RefreshState.RefreshFinish -> {}

            else -> {}
        }
    }
}