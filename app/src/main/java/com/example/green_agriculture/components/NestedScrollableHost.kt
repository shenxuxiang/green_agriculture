package com.example.green_agriculture.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.sign

/**
 * 官方推荐的解决 ViewPager2 嵌套滑动冲突的容器
 * 来源：https://github.com/android/views-widgets-samples
 */
class NestedScrollableHost : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f
    private var parentViewPager: ViewPager2? = null
    private var childView: View? = null

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    private fun getParentViewPager(): ViewPager2? {
        var v: View? = parent as? View
        while (v != null && v !is ViewPager2) {
            v = v.parent as? View
        }
        return v
    }

    private fun getChildView(): View? {
        return if (childCount > 0) getChildAt(0) else null
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (val child = getChildView()) {
            is RecyclerView -> {
                child.canScrollHorizontally(direction)
            }

            else -> {
                ViewCompat.canScrollHorizontally(child, direction)
            }
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        parentViewPager = getParentViewPager()
        childView = getChildView()

        if (parentViewPager == null || childView == null) {
            return
        }

        val orientation = parentViewPager!!.orientation

        // 早期拒绝：如果父 ViewPager2 不支持滑动，直接返回
        if (!parentViewPager!!.isUserInputEnabled) {
            return
        }

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = e.x
                initialY = e.y
                parentViewPager!!.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - initialX
                val dy = e.y - initialY
                val isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL

                // 计算实际滑动距离
                val scaledDx = abs(dx) * if (isVpHorizontal) 1f else 1f
                val scaledDy = abs(dy) * if (isVpHorizontal) 1f else 1f

                if (isVpHorizontal && scaledDx > touchSlop && scaledDx > scaledDy) {
                    // 水平滑动
                    if (canChildScroll(orientation, dx)) {
                        // 子 View 还能滑动，禁止父 ViewPager2 拦截
                        parentViewPager!!.requestDisallowInterceptTouchEvent(true)
                    } else {
                        // 子 View 不能滑动了，允许父 ViewPager2 拦截
                        parentViewPager!!.requestDisallowInterceptTouchEvent(false)
                    }
                } else if (!isVpHorizontal && scaledDy > touchSlop && scaledDy > scaledDx) {
                    // 垂直滑动
                    if (canChildScroll(orientation, dy)) {
                        parentViewPager!!.requestDisallowInterceptTouchEvent(true)
                    } else {
                        parentViewPager!!.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parentViewPager!!.requestDisallowInterceptTouchEvent(false)
            }
        }
    }
}