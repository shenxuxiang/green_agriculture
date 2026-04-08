package com.example.green_agriculture.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.isNotEmpty
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.SwiperViewAdapter
import com.example.green_agriculture.toolkit.CalculateUtils

data class SwiperViewItemOption(val url: String)

@SuppressLint("ClickableViewAccessibility")
class SwiperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val indicator: LinearLayout
    private val viewPager: ViewPager2
    private val adapter = SwiperViewAdapter()

    /**
     * 指针的颜色，indicatorPrimaryColor 表示高亮时的颜色
     */
    private val indicatorPrimaryColor = context.getColor(R.color.primary)
    private val indicatorColor = Color.parseColor("#A0FFFFFF")

    /**
     * index 的安全范围
     */
    private var indexSafeRange = IntRange(1, 1)
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

            indexSafeRange = IntRange(1, newList.size - 2)

            adapter.submitList(newList)
            initIndicator()
        }

    var indicatorIndex: Int = 0
        set(value) {
            if (field != value) {
                updateIndicator(value, field)
                field = value
            }

            if (viewPager.currentItem != value + 1) {
                viewPager.setCurrentItem(value + 1, true)
            }
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
                layoutParams = LinearLayout.LayoutParams(
                    CalculateUtils.dpToPx(if (index == indicatorIndex) 16 else 6).toInt(),
                    CalculateUtils.dpToPx(6).toInt(),
                ).apply {
                    setMargins(
                        CalculateUtils.dpToPx(6).toInt(),
                        0,
                        CalculateUtils.dpToPx(6).toInt(),
                        0,
                    )
                }
                background = GradientDrawable().apply {
                    cornerRadius = CalculateUtils.dpToPx(3)
                    setColor(if (index == indicatorIndex) indicatorPrimaryColor else indicatorColor)
                }
            }

            indicator.addView(view)
        }
    }

    /**
     * 更新指针的样式
     */
    private fun updateIndicator(newIndex: Int, oldIndex: Int) {
        val oldView = indicator.getChildAt(oldIndex)
        val newView = indicator.getChildAt(newIndex)

        // 保存 LayoutParams
        val oldParams = oldView.layoutParams
        val newParams = newView.layoutParams

        // 修改宽度
        oldParams.width = CalculateUtils.dpToPx(6).toInt()
        newParams.width = CalculateUtils.dpToPx(16).toInt()

        // 重新设置 LayoutParams
        oldView.layoutParams = oldParams
        newView.layoutParams = newParams

        // 修改背景色
        oldView.background = GradientDrawable().apply {
            cornerRadius = CalculateUtils.dpToPx(3)
            setColor(indicatorColor)
        }

        newView.background = GradientDrawable().apply {
            cornerRadius = CalculateUtils.dpToPx(3)
            setColor(indicatorPrimaryColor)
        }
    }

    private fun updateIndicatorWithFraction(newIndex: Int, oldIndex: Int, fraction: Float) {
        val oldView = indicator.getChildAt(oldIndex)
        val newView = indicator.getChildAt(newIndex)

        // 保存 LayoutParams
        val oldParams = oldView.layoutParams
        val newParams = newView.layoutParams

        // 修改宽度
        oldParams.width = CalculateUtils.dpToPx(6 + 10 * (1 - fraction)).toInt()
        newParams.width = CalculateUtils.dpToPx(6 + 10 * fraction).toInt()

        // 重新设置 LayoutParams
        oldView.layoutParams = oldParams
        newView.layoutParams = newParams

        // 修改背景色
        oldView.background = GradientDrawable().apply {
            cornerRadius = CalculateUtils.dpToPx(3)
            setColor(indicatorColor)
        }

        newView.background = GradientDrawable().apply {
            cornerRadius = CalculateUtils.dpToPx(3)
            setColor(indicatorPrimaryColor)
        }
    }


    /**
     * 监听滑动手势
     */
    private val handleScroll = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            Log.d("GA_APP", "position: $position, positionOffset: $positionOffset")
            //if (position !in indexSafeRange || positionOffset == 0f) return

            val index = position - 1
            Log.d("GA_APP", "indicatorIndex: $indicatorIndex")
            if (index == indicatorIndex) {
                // 往左边拖拽，此时 positionOffset 从 1 -> 0 逐渐减小
                val newIndex = (indicatorIndex + 1).takeIf { it < indexSafeRange.last } ?: 0
                val oldIndex = indicatorIndex
                updateIndicatorWithFraction(newIndex, oldIndex, positionOffset)
            } else {
                // 往右边拖拽，此时 positionOffset 从 0 -> 1 逐渐增大
                val newIndex = (indicatorIndex - 1).takeIf { it >= 0 } ?: (indexSafeRange.last - 1)
                val oldIndex = indicatorIndex

                updateIndicatorWithFraction(newIndex, oldIndex, 1 - positionOffset)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)

            if (state == SCROLL_STATE_IDLE) {
                val idx = viewPager.currentItem

                if (idx < indexSafeRange.first) {
                    viewPager.setCurrentItem(indexSafeRange.last, false)
                    indicatorIndex = indexSafeRange.last - 1
                } else if (idx > indexSafeRange.last) {
                    viewPager.setCurrentItem(indexSafeRange.first, false)
                    indicatorIndex = indexSafeRange.first - 1
                } else {
                    indicatorIndex = idx - 1
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewPager.registerOnPageChangeCallback(handleScroll)
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


