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

class IconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var iconName: String = ""
        set(value: String) {
            if (value == field) return
            field = value
            text = iconName
        }

    var iconSize: Float = CalculateUtils.dpToPx(16)
        set(value: Float) {
            if (value == field) return
            field = value
            setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    @ColorInt
    var iconColor: Int = context.getColor(R.color.black3)
        set(value: Int) {
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
        fun bindIconName(view: IconView, iconName: String) {
            view.iconName = iconName
        }

        @JvmStatic
        @BindingAdapter("iconColor")
        fun bindIconColor(view: IconView, iconColor: Int) {
            view.iconColor = iconColor
        }

        @JvmStatic
        @BindingAdapter("iconSize")
        fun bindIconSize(view: IconView, iconSize: Float) {
            view.iconSize = iconSize
        }
    }
}