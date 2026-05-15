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

/**
 * ButtonWidget
 * 为什么要使用 FrameLayout 嵌套一层？？？
 * 这是因为直接使用 MaterialButton 的话，用户点击时那种涟漪效果始终出不来，这才需要嵌套。
 */
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
    private val button: MaterialButton
    private val mask: View

    var onClickListener: (() -> Unit)? = null

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

    var type: String = "primary"

    var ghost: Boolean = false

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_button_widget, this, true).apply {
            button = findViewById(R.id.button)
        }

        updateButtonStyle()
        button.setOnClickListener {
            onClickListener?.invoke()
        }

        // 给 Button 添加一个背景遮罩，模拟 disabled 时的样式
        mask = View(context).apply {
            visibility = GONE
            setBackgroundColor(0x66FFFFFF)
        }

        addView(mask)
    }

    /**
     * 更新样式
     */
    fun updateButtonStyle() {
        val backgroundColor: ColorStateList
        val strokeColor: ColorStateList
        val rippleColor: ColorStateList
        val strokeWidth: Int
        val textColor: Int

        when (type) {
            "primary" -> { // primary
                rippleColor = ColorStateList.valueOf(if (ghost) 0x44DDDDDD else 0x22FFFFFF)
                backgroundColor = ColorStateList.valueOf(if (ghost) white else primaryColor)
                strokeColor = ColorStateList.valueOf(primaryColor)
                textColor = if (ghost) primaryColor else white
                strokeWidth = if (ghost) 1.dp.toInt() else 0
            }

            "danger" -> { //
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
            view.onClickListener = onClick
        }

        @JvmStatic
        @BindingAdapter("text")
        fun bindText(view: ButtonWidget, text: String) {
            view.button.text = text
        }

        @BindingAdapter("radius")
        fun bindRadius(view: ButtonWidget, radius: Int) {
            view.radius = radius.dp
        }

        @JvmStatic
        @BindingAdapter("type", "ghost", requireAll = false)
        fun bindTypeOrGhost(view: ButtonWidget, type: String = "primary", ghost: Boolean = false) {
            if (type != view.type || ghost != view.ghost) {
                view.type = type
                view.ghost = ghost
                view.updateButtonStyle()
            }
        }
    }
}