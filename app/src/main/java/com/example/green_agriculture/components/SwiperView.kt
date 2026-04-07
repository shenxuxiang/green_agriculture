package com.example.green_agriculture.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.green_agriculture.R
import com.example.green_agriculture.databinding.SwiperViewItemLayoutBinding

data class SwiperViewItemOption(val url: String)

@SuppressLint("ClickableViewAccessibility")
class SwiperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val viewPager: ViewPager2
    private val adapter = SwiperViewAdapter()

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

            adapter.submitList(newList)
        }

    var index: Int = 0
        set(value) {
            Log.d("GA_APP", "=====================set index value: $value, $field")
            if (field == value) return
            field = value

            if (viewPager.currentItem == value) return
            viewPager.setCurrentItem(value, true)
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
            viewPager = findViewById<ViewPager2>(R.id.view_pager)
            viewPager.offscreenPageLimit = 3
            viewPager.adapter = adapter
        }
    }

    private val handleScroll = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)

            if (state == SCROLL_STATE_IDLE) {
                val itemCount = adapter.itemCount
                val currentItem = viewPager.currentItem
     
                if (currentItem == 0) {
                    viewPager.setCurrentItem(itemCount - 2, false)
                } else if (currentItem == itemCount - 1) {
                    viewPager.setCurrentItem(1, false)
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
            view.index = index + 1
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

private class SwiperViewAdapter() :
    ListAdapter<SwiperViewItemOption, SwiperViewAdapter.SwiperItemViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwiperItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.swiper_view_item_layout,
            parent,
            false
        )

        val binding = SwiperViewItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )


        return SwiperItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SwiperItemViewHolder, position: Int) {
        val option = getItem(position)
        Glide.with(holder.itemView.context)
            .load(option.url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)
    }

    class DiffCallback : DiffUtil.ItemCallback<SwiperViewItemOption>() {
        override fun areItemsTheSame(
            oldItem: SwiperViewItemOption,
            newItem: SwiperViewItemOption,
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: SwiperViewItemOption,
            newItem: SwiperViewItemOption,
        ): Boolean {
            return oldItem == newItem
        }

    }

    class SwiperItemViewHolder(binding: SwiperViewItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
    }
}