package com.example.green_agriculture.pages.main.components

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.example.green_agriculture.R
import com.example.green_agriculture.components.IconWidget

class BottomNavigationBarItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val tabIcon: IconWidget
    private val tabName: TextView
    var tabSelected: Boolean = false
        set(value) {
            if (value == field) return
            field = value

            /**
             * 选中后样式修改：
             * 颜色高亮
             * 字体加粗
             */
            if (value) {
                color = context.getColor(R.color.primary)
                tabName.typeface = Typeface.DEFAULT_BOLD
            } else {
                color = context.getColor(R.color.black4)
                tabName.typeface = Typeface.DEFAULT
            }
        }

    @ColorInt
    var color: Int = context.getColor(R.color.black4)
        set(value) {
            if (value == field) return
            field = value
            tabIcon.iconColor = value
            tabName.setTextColor(value)
        }

    var icon: String = ""
        set(value) {
            if (value == field) return
            field = value
            tabIcon.iconName = value
        }

    var title: String = ""
        set(value) {
            if (value == field) return
            field = value
            tabName.text = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_tab_navigation_item, this, true)
            .apply {
                tabIcon = findViewById(R.id.tabIcon)
                tabName = findViewById(R.id.tabName)
            }

        orientation = VERTICAL
        gravity = Gravity.CENTER
    }
}