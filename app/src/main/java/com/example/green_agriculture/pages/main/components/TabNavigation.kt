package com.example.green_agriculture.pages.main.components

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Space
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.VibratorUtils

data class TabNavigationItemOption(
    val icon: Int,
    val label: String,
    val selectedIcon: Int,
)

class TabNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var onClickListener: ((Int) -> Unit)? = null

    private fun onTabItemClick(option: TabNavigationItemOption) {
        val index = tabOptions.indexOf(option)
        onClickListener?.invoke(index)
    }

    var tabOptions: List<TabNavigationItemOption> = emptyList()
        set(value: List<TabNavigationItemOption>) {
            if (value == field) return
            field = value

            if (childCount > 0) removeAllViews()
            value.forEachIndexed { index, item ->
                val itemView = createTabItemView(item, index == tabIndex)
                addView(itemView)

                if (index < value.size - 1) {
                    val space = Space(context)
                    space.layoutParams = LayoutParams(0, 0).apply { weight = 1f }
                    addView(space)
                }
            }
        }

    var tabIndex: Int = 0
        set(value: Int) {
            if (value == field) return
            field = value
            for (index in 0 until childCount) {
                val view = getChildAt(index)
                if (view is TabNavigationItem) {
                    val tabIndex = index / 2
                    val option = tabOptions[tabIndex]
                    val isSelected = tabIndex == value
                    if (view.tabSelected == isSelected) continue

                    // 状态更新
                    view.tabSelected = isSelected
                    view.icon =
                        context.getString(if (isSelected) option.selectedIcon else option.icon)
                    val anim = AnimationUtils.loadAnimation(
                        context,
                        if (isSelected) R.anim.navigation_item_scale_in_anim else R.anim.navigation_item_scale_out_anim
                    )

                    // 开始动画
                    view.startAnimation(anim)
                }
            }
        }

    /**
     * 创建 TabItemView
     */
    private fun createTabItemView(
        option: TabNavigationItemOption,
        isSelected: Boolean,
    ): TabNavigationItem {
        return TabNavigationItem(context).apply {
            title = option.label
            tabSelected = isSelected
            icon = context.getString(if (isSelected) option.selectedIcon else option.icon)

            // 绑定点击事件
            setOnClickListener {
                onTabItemClick(option)
                VibratorUtils.oneShot()
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("options")
        fun setOptionsAttr(view: TabNavigation, options: List<TabNavigationItemOption>) {
            view.tabOptions = options
        }

        @JvmStatic
        @BindingAdapter("index")
        fun setIndexAttr(view: TabNavigation, index: Int) {
            view.tabIndex = index
        }

        @JvmStatic
        @BindingAdapter("onChanged")
        fun setOnClickAttr(view: TabNavigation, onChanged: (Int) -> Unit) {
            view.onClickListener = onChanged
        }
    }
}
