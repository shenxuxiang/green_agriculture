package com.example.green_agriculture.components

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.dp
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
    private var canRefresh = false
    private val radius = 10.dp
    private val thickness = 2.dp
    private val tips: TextView = TextView(context).apply {
        layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }

        text = "刷新成功"
        setTextColor(context.getColor(R.color.black9))
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
    }

    private val indicator: CircularProgressIndicator = CircularProgressIndicator(context).apply {
        layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            }
        progress = 0
        // 消除 MaterialComponents 样式自带的间隙
        indicatorInset = 0
        // 暂停动画
        isIndeterminate = false
        indicatorSize = radius.toInt() * 2
        trackThickness = thickness.toInt()
        trackCornerRadius = thickness.toInt() / 2
        // 设置 CircularProgressIndicator 高亮颜色
        setIndicatorColor(context.getColor(R.color.primary))
        // 设置轨迹颜色
        trackColor = context.getColor(R.color.tertiaryPrimary)
    }

    init {
        addView(tips)
        addView(indicator)
    }

    override fun getView() = this

    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate

    @SuppressLint("RestrictedApi")
    override fun setPrimaryColors(vararg p0: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onInitialized(p0: RefreshKernel, p1: Int, p2: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int,
    ) {
        canRefresh = percent >= 1
        /**
         * offset 表示用户拖拽的距离
         * Indicator 的展示的位置，应该始终处于可是区域的中间位置
         * 这里使用 margin来调整 Indicator 的位置，不使用 translationY
         */
        val marginBottom = if (offset <= radius * 2) {
            0
        } else {
            (offset - radius * 2).coerceAtMost(height - radius * 2) / 2
        }

        /**
         * 被限定在 PullDownToRefresh 的过程中
         * 更新 Indicator 的进度、旋转角度、缩放大小
         */
        if (percent in 0f..1f) {
            val matrix = Matrix()
            val scale = percent.coerceAtMost(1f)
            matrix.postRotate(scale * 360f, radius, radius)
            matrix.postScale(scale, scale, radius, radius + (1 - scale) * radius)
            matrix.postTranslate(0f, -marginBottom.toFloat())
            indicator.animationMatrix = matrix
            indicator.progress = (scale * 100).toInt()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onReleased(
        refreshLayout: RefreshLayout,
        height: Int,
        maxDragHeight: Int,
    ) {
        if (canRefresh) {
            indicator.isIndeterminate = true
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onStartAnimator(
        p0: RefreshLayout,
        p1: Int,
        p2: Int,
    ) {
    }

    @SuppressLint("RestrictedApi")
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        if (success) {
            tips.text = "刷新成功"
        } else {
            tips.text = "刷新失败"
        }

        /**
         * 隐藏 Indicator
         */
        indicator.visibility = GONE
        indicator.isIndeterminate = false

        /**
         * 动画展示提示文案
         * 动画持续时间 300ms
         */
        val translationY = ObjectAnimator.ofFloat(tips, "translationY", (-20).dp, 0f)
        val alpha = ObjectAnimator.ofFloat(tips, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(tips, "scaleX", 0.6f, 1f)
        val scaleY = ObjectAnimator.ofFloat(tips, "scaleY", 0.6f, 1f)
        val animSet = AnimatorSet().apply {
            play(translationY).with(alpha).with(scaleX).with(scaleY)
            setDuration(300)
        }

        animSet.start()
        return 1000
    }

    @SuppressLint("RestrictedApi")
    override fun onHorizontalDrag(p0: Float, p1: Int, p2: Int) {
    }

    override fun isSupportHorizontalDrag() = false

    // autoOpen 必须设置为 false，否则调用 autoRefresh() 后不会触发下拉刷新、也不会展示下拉刷新动画
    override fun autoOpen(p0: Int, p1: Float, p2: Boolean) = false

    @SuppressLint("RestrictedApi")
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState,
    ) {
        /**
         * RefreshState.None              闲置状态，在 Header 由可见状态变为不可见状态后触发（并且用户释放了手指）。
         * RefreshState.PullDownToRefresh 下拉状态，此时还未触发下拉刷新的阈值
         * RefreshState.ReleaseToRefresh  触发下拉刷新阈值，此时释放手指即可触发刷新
         * RefreshState.RefreshFinish     刷新完成
         * 以上所有状态，在流程中只会触发一次，不会持续触发
         */
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                tips.alpha = 0f
                indicator.visibility = VISIBLE
            }

            RefreshState.ReleaseToRefresh -> {
                // 振动提示，提示用户可以释放手势
                VibratorUtils.oneShot()
            }

            else -> {}
        }
    }
}