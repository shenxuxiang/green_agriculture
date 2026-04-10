package com.example.green_agriculture.components

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
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
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import com.example.green_agriculture.R
import com.example.green_agriculture.adapter.SwiperWidgetAdapter
import com.example.green_agriculture.toolkit.CalculateUtils
import com.google.android.material.animation.AnimationUtils.lerp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

data class SwiperWidgetOptionItem(val url: String)

@SuppressLint("ClickableViewAccessibility")
class SwiperWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val indicator: LinearLayout
    private val viewPager: ViewPager2
    private val adapter = SwiperWidgetAdapter()

    var onIndexChangedListener: InverseBindingListener? = null

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
    private var positionSafeRange = IntRange.EMPTY

    /**
     * SwiperView 的配置项列表，要实现展示 [A, B, C, D] 并可循环播放；
     * 则实际的 ViewPager2 的子项配置应该为 [D, A, B, C, D, A]；ViewPager2 的默认展示项为 A（索引为 1）
     * 处在 A （index = 1）的位置时，用户向右拖拽完成后，立即调用 setCurrentItem(positionSafeRange.last, false)，虽然展示的还是 D，但实际索引却是 4；
     * 同理 D （index = 4）的位置时，用户向左拖拽完成后，立即调用 setCurrentItem(positionSafeRange.first, false)，虽然展示的还是 A，但实际索引却是 1；
     */
    var options: List<SwiperWidgetOptionItem> = emptyList()
        set(value) {
            if (value == field) return
            field = value

            val isLoopable = value.size > 1
            val newList = if (value.size > 1) {
                List(value.size + 2) {
                    when (it) {
                        0 -> value[value.size - 1]
                        value.size + 1 -> value[0]
                        else -> value[it - 1]
                    }
                }
            } else value

            positionSafeRange = if (isLoopable) IntRange(1, newList.size - 2) else IntRange.EMPTY

            adapter.submitList(newList)
            viewPager.isUserInputEnabled = isLoopable
            viewPager.setCurrentItem(
                if (isLoopable) indicatorIndex + 1 else 0,
                false
            )

            initIndicator()
        }

    // 当前指针索引
    var indicatorIndex: Int = 0
        set(value) {
            if (field == value) return
            field = value
            // 通知 DataBinding，index 改变了
            onIndexChangedListener?.onChange()
            updateIndicatorWithFraction(value, -1, 1f)

            // 如果当前 ViewPager2 的 position 与 Next Indicator Index 不匹配，那么就同步更新。
            if (getSafePosition(viewPager.currentItem) != value + 1) {
                viewPager.setCurrentItem(value + 1, false)
            }
        }

    // 间隔时间
    var intervalTimeout: Long = 5000
        set(value) {
            if (field == value) return
            field = value.coerceAtLeast(3000)
        }

    // 用户取消对应的协程作用域
    private var intervalJob: Job? = null

    // 间隔动画
    private var intervalAnimator: ValueAnimator? = null

    /**
     * 外部 ViewPager2
     * 当用户在 SwiperView 上滑动时，取消 outerViewPager 的 isUserInputEnabled 的行为
     */
    var outerViewPager2: ViewPager2? = null
        set(value) {
            if (field == value) return
            field = value
        }

    /**
     * 缓存指针指示器的所有 View、GradientDrawable
     * 缓存的目的在于：修改样式时不需要重复的创建 LayoutParams、以及 GradientDrawable
     */
    private val indicatorHolders = ArrayList<IndicatorViewHolder>()

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
        indicatorHolders.clear()
        if (indicator.isNotEmpty()) indicator.removeAllViews()
        if (options.size <= 1) return

        options.forEachIndexed { index, _ ->
            val width = if (index == indicatorIndex) indicatorHighlightWidth else indicatorWidth
            val color = if (index == indicatorIndex) indicatorHighlightColor else indicatorColor

            val drawable = GradientDrawable().apply {
                setColor(color)
                cornerRadius = indicatorCornerRadius
            }

            val view = View(context).apply {
                val height = indicatorWidth
                // 设置布局参数
                layoutParams = LinearLayout.LayoutParams(width, height).apply {
                    setMargins(indicatorGap, 0, indicatorGap, 0)
                }

                // 设置圆角、颜色
                background = drawable
            }

            indicator.addView(view)
            indicatorHolders.add(IndicatorViewHolder(view, drawable))
        }
    }

    /**
     * 更新指定的 IndicatorItem 的样式
     * index    - indicator 的索引位置
     * fraction - 样式过渡的比例：0-普通，1-高亮
     */
    private fun updateIndicatorItem(index: Int, fraction: Float) {
        indicatorHolders.getOrNull(index)?.let { holder ->
            // 保存 LayoutParams
            val lp = holder.view.layoutParams
            val width = lerp(indicatorWidth, indicatorHighlightWidth, fraction)
            // 修改宽度
            if (width != lp.width) lp.width = width

            /**
             * 重新设置 LayoutParams
             * View.setLayoutParams(value) 内部始终都会调用 requestLayout()；
             * 所以只要赋值了，UI 就会更新。
             */
            holder.view.layoutParams = lp

            val color =
                ArgbEvaluator().evaluate(fraction, indicatorColor, indicatorHighlightColor) as Int
            // 修改背景色
            holder.drawable.setColor(color)
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
                nextIndex -> updateIndicatorItem(i, fraction)
                currentIndex -> updateIndicatorItem(i, 1 - fraction)
                else -> updateIndicatorItem(i, 0f)
            }
        }
    }

    /**
     * 计算正确的位置索引
     * 如果 position 不在 positionSafeRange 范围内，则自动转换到一个安全的索引位置
     */
    private fun getSafePosition(position: Int): Int {
        if (options.size <= 1) return 0

        return if (position > positionSafeRange.last) {
            positionSafeRange.first
        } else if (position < positionSafeRange.first) {
            positionSafeRange.last
        } else {
            position
        }
    }

    /**
     * currentPositionSnapShot 在用户拖拽开始时，记录当前 position 的一个快照
     * currentPositionSnapShot 用于 onPageScrolled 事件中，与参数 position 对比，从而得出是向左还是向右拖拽。
     * isUserDragging 在用户拖拽开始时，设置为 true，表示此次行为是用户拖拽行为
     */
    private var currentPositionSnapShot = 0
    private var isUserDragging = false

    /**
     * 监听用户拖拽行为
     * 注意，用户是通过 viewPager2.setCurrentItem(positon, smoothScroll) 触发的页面跳转，也会触发
     * 当 smoothScroll == true，页面自动滑动的同时，会不断的触发 onPageScrolled，参数 positionOffset 会不停的变化
     * 当 smoothScroll == false，页面是瞬间完成跳转的，此时只会在完成时触发 onPageScrolled，并且参数 positionOffset == 0
     */
    private val handleScroll = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            if (options.size <= 1) return

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
                if (!isUserDragging) return
                /**
                 * position + positionOffset > currentPositionSnapShot 说明用户在往左拖拽，
                 * 往左边拖拽时， positionOffset 从 0 -> 1 逐渐增大，并且 position == currentPositionSnapShot
                 * 往右边拖拽时， positionOffset 从 1 -> 0 逐渐减小，并且 position == currentPositionSnapShot - 1
                 */
                if (position + positionOffset > currentPositionSnapShot) {
                    val nextIndex = getSafePosition(position + 1) - 1
                    val currentIndex = currentPositionSnapShot - 1
                    updateIndicatorWithFraction(nextIndex, currentIndex, positionOffset)
                } else {
                    val nextIndex = getSafePosition(position) - 1
                    val currentIndex = currentPositionSnapShot - 1

                    updateIndicatorWithFraction(nextIndex, currentIndex, 1 - positionOffset)
                }
            }
        }

        // 滑动状态变更，一个完整的事件流程，只会触发一次。
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            when (state) {
                SCROLL_STATE_DRAGGING -> { // 用户开始拖拽
                    cancelIntervalJob()
                    isUserDragging = true
                    currentPositionSnapShot = viewPager.currentItem
                }

                SCROLL_STATE_IDLE -> { // 拖拽、并且惯性滚动结束，此时页面处于禁止状态
                    startIntervalJob()
                    isUserDragging = false
                }

                SCROLL_STATE_SETTLING -> { // 拖拽结束（用户释放了手指）
                }
            }
        }
    }

    // 开始间隔任务
    fun startIntervalJob() {
        findViewTreeLifecycleOwner()?.let {
            if (intervalJob?.isCancelled ?: true)
                intervalJob = it.lifecycleScope.launch {
                    it.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        flow {
                            while (true) {
                                delay(intervalTimeout)
                                emit(true)
                            }
                        }.collect {
                            if (options.size > 1) {
                                val position = viewPager.currentItem
                                val nextPosition = viewPager.currentItem + 1
                                // 执行 ViewPager2 跳转
                                viewPager.setCurrentItem(nextPosition, true)

                                val currentIndex = getSafePosition(position) - 1
                                val nextIndex = getSafePosition(position + 1) - 1
                                // 执行 Indicator 动画
                                intervalAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                                    duration = 300
                                    interpolator = FastOutSlowInInterpolator()
                                    addUpdateListener {
                                        updateIndicatorWithFraction(
                                            nextIndex,
                                            currentIndex,
                                            animatedValue as Float
                                        )
                                    }
                                    start()
                                }
                            }
                        }
                    }
                }
        }
    }

    // 取消间隔任务
    private fun cancelIntervalJob() {
        intervalJob?.cancel()
        intervalJob = null
        intervalAnimator?.cancel()
        intervalAnimator = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewPager.registerOnPageChangeCallback(handleScroll)
        startIntervalJob()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewPager.unregisterOnPageChangeCallback(handleScroll)
        cancelIntervalJob()
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
        fun setOptionsAttr(view: SwiperWidget, options: List<SwiperWidgetOptionItem>, index: Int) {
            view.options = options
            view.indicatorIndex = index
        }

        @JvmStatic
        @BindingAdapter("interval")
        fun setIntervalAttr(view: SwiperWidget, interval: Long) {
            view.intervalTimeout = interval
        }

        @JvmStatic
        @BindingAdapter("outer_view_pager")
        fun setOuterViewPagerAttr(view: SwiperWidget, outerViewPager: ViewPager2) {
            view.outerViewPager2 = outerViewPager
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "index", event = "indexAttrChanged")
        fun getIndexAttr(view: SwiperWidget): Int {
            return view.indicatorIndex
        }

        @JvmStatic
        @BindingAdapter("indexAttrChanged")
        fun setIndexAttrChanged(view: SwiperWidget, listener: InverseBindingListener) {
            view.onIndexChangedListener = listener
        }
    }
}

private data class IndicatorViewHolder(val view: View, val drawable: GradientDrawable)