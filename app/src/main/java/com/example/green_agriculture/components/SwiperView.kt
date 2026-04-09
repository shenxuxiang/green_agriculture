package com.example.green_agriculture.components

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.isNotEmpty
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.SwiperViewAdapter
import com.example.green_agriculture.toolkit.CalculateUtils
import com.google.android.material.animation.AnimationUtils.lerp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

data class SwiperViewItemOption(val url: String)

@SuppressLint("ClickableViewAccessibility")
class SwiperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val indicator: LinearLayout
    private val viewPager: ViewPager2
    private val adapter = SwiperViewAdapter()

    /**
     * 定义指针的颜色、大小、圆角半径、间隙
     */
    private val indicatorColor = 0xBBFFFFFF.toInt()
    private val indicatorHighlightColor = context.getColor(R.color.primary)
    private val indicatorCornerRadius = CalculateUtils.dpToPx(dp = 3, context)
    private val indicatorWidth = CalculateUtils.dpToPx(dp = 6, context).toInt()
    private val indicatorHighlightWidth = CalculateUtils.dpToPx(dp = 16, context).toInt()
    private val indicatorGap = CalculateUtils.dpToPx(dp = 6, context).toInt()

    /**
     * index 的安全范围
     * SwiperView 的配置项列表，要实现展示 [A, B, C, D] 并可循环播放；
     * 则实际的 ViewPager2 的子项配置应该为 [D, A, B, C, D, A]；ViewPager2 的默认展示项为 A（索引为 1）
     * 处在 A （index = 1）的位置时，用户向右拖拽完成后来到了 D（index = 0），为了下一次还能够向右拖拽，应该立即跳转到 index = 4 位置，此时展示的还是 D，对于用户是无感的；
     * 同理 D （index = 4）的位置时，用户向左拖拽完成后，也应该立即跳转到 index = 1 位置；
     * 综合上述，其实有一个安全范围（这里是1-4）是不需要执行上述操作的；
     */
    private var positionSafeRange = IntRange(1, 1)

    /**
     * SwiperView 的配置项列表，要实现展示 [A, B, C, D] 并可循环播放；
     * 则实际的 ViewPager2 的子项配置应该为 [D, A, B, C, D, A]；ViewPager2 的默认展示项为 A（索引为 1）
     * 处在 A （index = 1）的位置时，用户向右拖拽完成后，立即调用 setCurrentItem(positionSafeRange.last, false)，虽然展示的还是 D，但实际索引却是 4；
     * 同理 D （index = 4）的位置时，用户向左拖拽完成后，立即调用 setCurrentItem(positionSafeRange.first, false)，虽然展示的还是 A，但实际索引却是 1；
     */
    var options: List<SwiperViewItemOption> = emptyList()
        set(value) {
            if (value == field) return
            field = value

            val newList = if (value.size > 1) {
                List(value.size + 2) {
                    when (it) {
                        0 -> value[value.size - 1]
                        value.size + 1 -> value[0]
                        else -> value[it - 1]
                    }
                }
            } else value

            positionSafeRange = IntRange(1, newList.size - 2)

            adapter.submitList(newList)
            viewPager.setCurrentItem(indicatorIndex + 1, false)

            initIndicator()
        }

    var indicatorIndex: Int = 0
        set(value) {
            if (field == value) return
            field = value

            updateIndicatorWithFraction(value, -1, 1f)
        }

    var intervalTimeout: Long = 5000
        set(value) {
            if (field == value) return
            field = value
        }

    /**
     * 外部 ViewPager2
     * 当用户在 SwiperView 上滑动时，取消 outerViewPager 的 isUserInputEnabled 的行为
     */
    var outerViewPager2: ViewPager2? = null
        set(value) {
            if (field == value) return
            field = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.swiper_view_layout, this, true).apply {
            indicator = findViewById<LinearLayout>(R.id.indicator)
            viewPager = findViewById<ViewPager2>(R.id.view_pager)
            viewPager.offscreenPageLimit = 3
            viewPager.adapter = adapter
        }
    }

    /**
     * 初始化 SwiperView 指针样式
     */
    private fun initIndicator() {
        if (indicator.isNotEmpty()) indicator.removeAllViews()
        options.forEachIndexed { index, _ ->
            val view = View(context).apply {
                val height = indicatorWidth
                val width = if (index == indicatorIndex) indicatorHighlightWidth else indicatorWidth
                val color = if (index == indicatorIndex) indicatorHighlightColor else indicatorColor
                // 设置布局参数
                layoutParams = LinearLayout.LayoutParams(width, height).apply {
                    setMargins(indicatorGap, 0, indicatorGap, 0)
                }

                // 设置圆角、颜色
                background = GradientDrawable().apply {
                    setColor(color)
                    cornerRadius = indicatorCornerRadius
                }
            }

            indicator.addView(view)
        }
    }

    /**
     * 更新指定的 IndicatorItem 的样式
     * index    - indicator 的索引位置
     * fraction - 样式过渡的比例：0-普通，1-高亮
     */
    private fun updateIndicatorItem(index: Int, fraction: Float) {
        indicator.getChildAt(index)?.let { view ->
            // 保存 LayoutParams
            val newParams = view.layoutParams

            // 修改宽度
            newParams.width = lerp(indicatorWidth, indicatorHighlightWidth, fraction)

            // 重新设置 LayoutParams
            view.layoutParams = newParams

            // 修改背景色
            view.background = GradientDrawable().apply {
                cornerRadius = indicatorCornerRadius
                setColor(
                    ArgbEvaluator().evaluate(
                        fraction,
                        indicatorColor,
                        indicatorHighlightColor
                    ) as Int
                )
            }
        }
    }

    /**
     * 更新所有 Indicator 的样式；
     * nextIndex    - 表示即将过渡的目标 IndicatorItem
     * currentIndex - 当前展示高亮的 IndicatorItem
     * fraction     - 过渡比例
     */
    private fun updateIndicatorWithFraction(nextIndex: Int, currentIndex: Int, fraction: Float) {
        options.forEachIndexed { i, _ ->
            when (i) {
                nextIndex -> {
                    updateIndicatorItem(i, fraction)
                }

                currentIndex -> {
                    updateIndicatorItem(i, 1 - fraction)
                }

                else -> {
                    updateIndicatorItem(i, 0f)
                }
            }
        }
    }

    private fun getSafePosition(position: Int): Int {
        return if (position > positionSafeRange.last) {
            positionSafeRange.first
        } else if (position < positionSafeRange.first) {
            positionSafeRange.last
        } else {
            position
        }
    }


    private var currentPosition = 0;

    /**
     * 监听滑动手势
     */
    private val handleScroll = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            val safePosition = getSafePosition(position)
            /**
             * positionOffset == 0 说明当前 ViewPager2 动画执行结束，界面切换已完成；
             * 并且，当 positionOffset == 0 时，需要判断当前 ViewPager2 是否处于 positionSafeRange 范围内；
             * 及时调整，这样 ViewPager2 才可以实现无限滑动。
             * 同步更新 indicatorIndex，以及 Indicator 的样式。
             */
            if (positionOffset == 0f) {
                if (position > positionSafeRange.last) {
                    viewPager.setCurrentItem(positionSafeRange.first, false)
                } else if (position < positionSafeRange.first) {
                    viewPager.setCurrentItem(positionSafeRange.last, false)
                }

                indicatorIndex = safePosition - 1
            } else {
                /**
                 * position + positionOffset > currentPosition 说明用户在往左拖拽，
                 * 往左边拖拽时， positionOffset 从 0 -> 1 逐渐增大，并且 position == currentPosition
                 * 往右边拖拽时， positionOffset 从 1 -> 0 逐渐减小，并且 position == currentPosition - 1
                 */
                if (position + positionOffset > currentPosition) {
                    val nextIndex = getSafePosition(position + 1) - 1
                    val currentIndex = currentPosition - 1
                    updateIndicatorWithFraction(nextIndex, currentIndex, positionOffset)
                } else {
                    val nextIndex = getSafePosition(position) - 1
                    val currentIndex = currentPosition - 1

                    updateIndicatorWithFraction(nextIndex, currentIndex, 1 - positionOffset)
                }
            }

        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == SCROLL_STATE_DRAGGING) {
                currentPosition = viewPager.currentItem
            }
//            if (state == SCROLL_STATE_IDLE) {
//                val idx = viewPager.currentItem
//
//                if (idx < indexSafeRange.first) {
//                    viewPager.setCurrentItem(indexSafeRange.last, false)
//                    indicatorIndex = indexSafeRange.last - 1
//                } else if (idx > indexSafeRange.last) {
//                    viewPager.setCurrentItem(indexSafeRange.first, false)
//                    indicatorIndex = indexSafeRange.first - 1
//                } else {
//                    indicatorIndex = idx - 1
//                }
//            }
        }
    }

    private val intervalFlow = flow {
        while (true) {
            delay(intervalTimeout)
            emit(true)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewPager.registerOnPageChangeCallback(handleScroll)
        findViewTreeLifecycleOwner()?.let {
            it.lifecycleScope.launch {
                it.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    flow {
                        while (true) {
                            delay(intervalTimeout)
                            emit(true)
                        }
                    }.collect {

                    }
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewPager.unregisterOnPageChangeCallback(handleScroll)
    }

    /**
     * 重写 SwiperView 的 dispatchTouchEvent
     * 当用户手指按下时，设置 outerViewPager2.isUserInputEnabled 为 false，禁止用户操作 outerViewPager2。
     * 当所有是指都抬起，设置 outerViewPager2.isUserInputEnabled 为 true，允许用户操作 outerViewPager2。
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        outerViewPager2?.let {
            val type = ev?.actionMasked
            if (type == MotionEvent.ACTION_DOWN) {
                it.isUserInputEnabled = false
            } else if (type == MotionEvent.ACTION_UP || type == MotionEvent.ACTION_CANCEL) {
                it.isUserInputEnabled = true
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("options", "index")
        fun setOptionsAttr(view: SwiperView, options: List<SwiperViewItemOption>, index: Int) {
            view.options = options
            view.indicatorIndex = index
        }

//        @JvmStatic
//        @BindingAdapter("index")
//        fun setIndexAttr(view: SwiperView, index: Int) {
//            view.index = index + 1
//        }

        @JvmStatic
        @BindingAdapter("outer_view_pager")
        fun setOuterViewPagerAttr(view: SwiperView, outerViewPager: ViewPager2) {
            view.outerViewPager2 = outerViewPager
        }


    }
}


