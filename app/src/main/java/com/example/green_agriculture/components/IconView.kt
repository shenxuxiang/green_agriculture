package com.example.green_agriculture.components

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
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
            0
        )

        typedArray.getDimension(R.styleable.IconView_iconSize, -1f).also {
            if (it > 0) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
                setLineHeight(TypedValue.COMPLEX_UNIT_PX, it)
            }
        }

        typedArray.getString(R.styleable.IconView_iconName)?.let {
            text = it
        }

        typedArray.getColor(R.styleable.IconView_iconColor, R.color.black6).also {
            setTextColor(it)
        }

        setTypeface(Typeface.createFromAsset(context.assets, "font/icon_font.ttf"))
    }
}