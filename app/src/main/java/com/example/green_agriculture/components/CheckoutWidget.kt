package com.example.green_agriculture.components

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.example.green_agriculture.R
import com.example.green_agriculture.extend.dp

class CheckoutWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    var radius: Float = 6.dp
        set(value) {
            if (value == field) return

            field = value
        }

    var size: Float = 18.dp
        set(value) {
            if (value == field) return

            field = value
        }

    init {
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CheckoutWidget, defStyleAttr, 0)

            radius = typedArray.getDimension(R.styleable.CheckoutWidget_radius, 6.dp)

            size = typedArray.getDimension(R.styleable.CheckoutWidget_size, 18.dp)

            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        val rectF = RectF()
//        canvas.drawRoundRect()
    }
}