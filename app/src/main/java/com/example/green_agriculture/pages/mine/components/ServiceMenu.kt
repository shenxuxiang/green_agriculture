package com.example.green_agriculture.pages.mine.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.entity.MenuItemOption
import com.example.green_agriculture.extend.dp

class ServiceMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : GridLayout(context, attrs, defStyleAttr) {
    var menus: List<MenuItemOption> = emptyList()
        set(value) {
            if (value == field) return
            field = value

            removeAllViews()
            for (option in value) {
                addChild(option)
            }
        }

    fun addChild(option: MenuItemOption): LinearLayout {
        val avatar = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(24.dp.toInt(), 24.dp.toInt())
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(option.resId)
        }

        val text = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            text = option.title
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setPadding(0, 5.dp.toInt(), 0, 0)
            setTextColor(ContextCompat.getColor(context, R.color.black4))
        }

        val childContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LayoutParams().apply {
                width = 0
                height = LayoutParams.WRAP_CONTENT
                columnSpec = spec(UNDEFINED, 1f)
                setMargins(0, 21.dp.toInt(), 0, 0)
            }
            addView(avatar)
            addView(text)
        }

        addView(childContainer)

        return childContainer
    }

    companion object {
        @JvmStatic
        @BindingAdapter("menus")
        fun bindMenus(view: ServiceMenu, menus: List<MenuItemOption>) {
            view.menus = menus
        }
    }
}