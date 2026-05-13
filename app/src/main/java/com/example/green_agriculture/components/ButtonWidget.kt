package com.example.green_agriculture.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.dp
import com.google.android.material.button.MaterialButton

class ButtonWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val primaryColor = ContextCompat.getColor(context, R.color.primary)
    private val errorColor = ContextCompat.getColor(context, R.color.error)
    private val black9 = ContextCompat.getColor(context, R.color.black9)
    private val black4 = ContextCompat.getColor(context, R.color.black4)
    private val white = ContextCompat.getColor(context, R.color.white)
    private var onClick: () -> Unit = {}
    private val mask: View
    private val button: MaterialButton

    var buttonEnabled: Boolean = true
        set(value) {
            if (value == field) return
            field = value

            button.isEnabled = value
            mask.visibility = if (value) GONE else VISIBLE
        }

    var radius: Float = 0f
        set(value) {
            if (value == field) return
            field = value

            button.cornerRadius = value.toInt()
            background = GradientDrawable().apply {
                cornerRadius = value
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_button_widget, this, true).apply {
            button = findViewById(R.id.button)
        }

        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.ButtonWidget, defStyleAttr, 0)

            val type = typedArray.getInt(R.styleable.ButtonWidget_type, 0)
            val ghost = typedArray.getBoolean(R.styleable.ButtonWidget_ghost, false)
            radius = typedArray.getDimension(R.styleable.ButtonWidget_radius, 18.dp)

            val backgroundColor: ColorStateList
            val strokeColor: ColorStateList
            val rippleColor: ColorStateList
            val strokeWidth: Int
            val textColor: Int

            when (type) {
                0 -> { // primary
                    rippleColor = ColorStateList.valueOf(if (ghost) 0x44DDDDDD else 0x22FFFFFF)
                    backgroundColor = ColorStateList.valueOf(if (ghost) white else primaryColor)
                    strokeColor = ColorStateList.valueOf(primaryColor)
                    textColor = if (ghost) primaryColor else white
                    strokeWidth = if (ghost) 1.dp.toInt() else 0
                }

                1 -> { // danger
                    rippleColor = ColorStateList.valueOf(if (ghost) 0x44DDDDDD else 0x22FFFFFF)
                    backgroundColor = ColorStateList.valueOf(if (ghost) white else errorColor)
                    strokeColor = ColorStateList.valueOf(errorColor)
                    textColor = if (ghost) errorColor else white
                    strokeWidth = if (ghost) 1.dp.toInt() else 0
                }

                else -> { // normal
                    rippleColor = ColorStateList.valueOf(0x44DDDDDD)
                    backgroundColor = ColorStateList.valueOf(white)
                    strokeColor = ColorStateList.valueOf(black9)
                    strokeWidth = 1.dp.toInt()
                    textColor = black4
                }
            }

            button.backgroundTintList = backgroundColor
            button.strokeColor = strokeColor
            button.rippleColor = rippleColor
            button.strokeWidth = strokeWidth
            button.setTextColor(textColor)
            button.setOnClickListener {
                onClick.invoke()
            }
        }

        // 添加一个背景遮罩
        mask = View(context).apply {
            visibility = GONE
            setBackgroundColor(0x66FFFFFF)
        }
        addView(mask)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("enabled")
        fun bindEnabled(view: ButtonWidget, enabled: Boolean) {
            view.buttonEnabled = enabled
        }

        @JvmStatic
        @BindingAdapter("onClick")
        fun bindOnClick(view: ButtonWidget, onClick: () -> Unit) {
            view.onClick = onClick
        }
    }
}