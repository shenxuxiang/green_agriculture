package com.example.green_agriculture.pages.login.components

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.databinding.LayoutLoginTabBinding
import com.example.green_agriculture.extend.dp
import com.example.green_agriculture.toolkit.VibratorUtils

class LoginTab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    val indicatorLayoutWidth: Float
    val textColor = context.getColor(R.color.black4)
    val textPrimaryColor = context.getColor(R.color.primary)
    var onChangeCallback: ((tabIndex: Int) -> Unit)? = null
    val binding = LayoutLoginTabBinding.inflate(LayoutInflater.from(context), this, true)
    var tabIndex: Int = 0
        set(value) {
            if (value == field) return
            field = value

            if (value == 0) {
                executeColorAnimation(binding.fastLogin, textColor, textPrimaryColor)
                executeColorAnimation(binding.accountLogin, textPrimaryColor, textColor)
            } else {
                executeColorAnimation(binding.fastLogin, textPrimaryColor, textColor)
                executeColorAnimation(binding.accountLogin, textColor, textPrimaryColor)
            }
            executeIndicatorAnimation(value)

            onChangeCallback?.invoke(value)
        }

    init {
        orientation = VERTICAL
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        indicatorLayoutWidth = (tabItemWidth * 2 + 76.dp) - (tabItemWidth - 20.dp)

        updateIndicatorLayout()
        binding.indicator.background = GradientDrawable().apply {
            cornerRadius = 2.dp
            setColor(textPrimaryColor)
        }

        binding.fastLogin.setOnClickListener {
            tabIndex = 0
            VibratorUtils.oneShot()
        }
        binding.accountLogin.setOnClickListener {
            tabIndex = 1
            VibratorUtils.oneShot()
        }
    }

    private val tabItemWidth: Int
        get() {
            binding.accountLogin.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            return binding.accountLogin.measuredWidth
        }

    /**
     * 初始化布局，动态计算出 indicatorLayout 元素的实际宽度，并水平居中
     */
    private fun updateIndicatorLayout() {
        val indicatorLayoutLP = binding.indicatorLayout.layoutParams
        indicatorLayoutLP.width = indicatorLayoutWidth.toInt()
        binding.indicatorLayout.layoutParams = indicatorLayoutLP
    }

    fun executeColorAnimation(view: TextView, startColor: Int, endColor: Int) {
        ObjectAnimator.ofArgb(
            view,
            "textColor",
            startColor,
            endColor,
        ).apply {
            duration = 200
            start()
        }
    }

    fun executeIndicatorAnimation(tabIndex: Int) {
        binding.indicator.animate()
            .translationX(if (tabIndex == 0) 0f else indicatorLayoutWidth - 20.dp)
            .setDuration(200)
            .start()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("tabIndex")
        fun bindTabIndex(view: LoginTab, tabIndex: Int) {
            view.tabIndex = tabIndex
        }

        @JvmStatic
        @BindingAdapter("onChanged")
        fun bindOnChanged(view: LoginTab, onChanged: (tabIndex: Int) -> Unit) {
            view.onChangeCallback = onChanged
        }
    }
}
