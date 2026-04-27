package com.example.green_agriculture.components

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.CalculateUtils

class IconWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var iconName: String = ""
        set(value) {
            if (value == field) return
            field = value
            text = iconName
        }

    var iconSize: Float = CalculateUtils.dpToPx(16)
        set(value) {
            if (value == field) return
            field = value
            setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    @ColorInt
    var iconColor: Int = context.getColor(R.color.black3)
        set(value) {
            if (value == field) return
            field = value
            setTextColor(value)
        }

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.IconView,
            defStyleAttr,
            0,
        ) {

            getDimension(R.styleable.IconView_iconSize, -1f).also {
                if (it > 0) iconSize = it
            }

            getString(R.styleable.IconView_iconName)?.let {
                iconName = it
            }

            getColor(R.styleable.IconView_iconColor, context.getColor(R.color.black4)).also {
                iconColor = it
            }

            setTypeface(Typeface.createFromAsset(context.assets, "font/icon_font.ttf"))
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("iconName")
        fun bindIconName(view: IconWidget, iconName: String) {
            view.iconName = iconName
        }

        @JvmStatic
        @BindingAdapter("iconColor")
        fun bindIconColor(view: IconWidget, iconColor: Int) {
            view.iconColor = iconColor
        }

        @JvmStatic
        @BindingAdapter("iconSize")
        fun bindIconSize(view: IconWidget, iconSize: Float) {
            view.iconSize = iconSize
        }
    }
}