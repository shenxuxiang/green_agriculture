package com.example.green_agriculture.components

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R

class IconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {
    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.IconView,
            defStyleAttr,
            0,
        )

        typedArray.getDimension(R.styleable.IconView_iconSize, -1f).also {
            if (it > 0) setIconSize(it)
        }

        typedArray.getString(R.styleable.IconView_iconName)?.let {
            setIconName(it)
        }

        typedArray.getColor(R.styleable.IconView_iconColor, R.color.black6).also {
            setTextColor(it)
        }

        setTypeface(Typeface.createFromAsset(context.assets, "font/icon_font.ttf"))
    }

    fun setIconName(iconName: String) {
        text = iconName
    }

    fun setIconColor(iconColor: Int) {
        setTextColor(iconColor)
    }

    fun setIconSize(iconSize: Float) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, iconSize)
        setLineHeight(TypedValue.COMPLEX_UNIT_PX, iconSize)
    }

    companion object {
        @BindingAdapter("iconName")
        fun setIconName(view: IconView, iconName: String) {
            view.setIconName(iconName)
        }

        @BindingAdapter("iconColor")
        fun setIconColor(view: IconView, iconColor: Int) {
            view.setIconColor(iconColor)
        }

        @BindingAdapter("iconSize")
        fun setIconSize(view: IconView, iconSize: Float) {
            view.setIconSize(iconSize)
        }
    }
}