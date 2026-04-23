package com.example.green_agriculture.pages.home.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.entity.MenuItemOption
import com.example.green_agriculture.toolkit.CalculateUtils


class MenuWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : GridLayout(context, attrs, defStyleAttr) {

    var menuOptions: List<MenuItemOption> = emptyList()
        set(value) {
            if (value == field) return

            field = value
            removeAllViews()

            value.forEachIndexed { index, option ->
                val menu = MenuItem(context)
                menu.title = option.title
                menu.image = option.resId
                if (index >= columnCount) menu.marginTop = 24

                addView(menu)
            }
        }

    init {
        background = GradientDrawable().apply {
            cornerRadius = CalculateUtils.dpToPx(10, context)
            setColor(0xFFFFFFFF.toInt())
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("menuOptions")
        fun bindMenuOptions(view: MenuWidget, menuOptions: List<MenuItemOption>) {
            view.menuOptions = menuOptions
        }
    }
}