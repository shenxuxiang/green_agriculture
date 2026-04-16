package com.example.green_agriculture.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.CalculateUtils

class ToastWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val titleView: TextView
    val paddingX = CalculateUtils.dpToPx(12, context).toInt()
    val paddingY = CalculateUtils.dpToPx(16, context).toInt()

    val corner = CalculateUtils.dpToPx(8, context)

    var title: String = ""
        set(value) {
            if (value == field) return

            field = value
            titleView.text = title
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.toast_widget, this, true).apply {
            titleView = findViewById(R.id.title)

            gravity = Gravity.START or Gravity.CLIP_VERTICAL

            setPadding(paddingX, paddingY, paddingX, paddingY)
            background = GradientDrawable().apply {
                setColor(0xAA000000.toInt())
                cornerRadius = corner
            }
        }
    }

    companion object {

    }
}