package com.example.green_agriculture.pages.home.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.green_agriculture.R
import com.example.green_agriculture.toolkit.CalculateUtils

class MenuItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView

    var image: Int = -1
        set(value) {
            if (value == field) return
            field = value

            if (image == -1) return
            Glide
                .with(context)
                .load(context.getDrawable(value))
                .into(imageView)
        }

    var title: String = ""
        set(value) {
            if (value == field) return
            field = value
            textView.text = value
        }

    var marginTop: Int = 0
        set(value) {
            if (value == field) return

            field = value

            val lp = layoutParams as GridLayout.LayoutParams
            lp.topMargin = CalculateUtils.dpToPx(value).toInt()

            layoutParams = lp
        }

    init {
        // 创建父容器布局参数
        val params = GridLayout.LayoutParams().apply {
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        }

        params.width = 0
        params.height = LayoutParams.WRAP_CONTENT

        layoutParams = params
        orientation = VERTICAL
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

        createImage()
        createTextView()
    }

    private fun createImage() {
        imageView = ImageView(context).apply {
            layoutParams = LayoutParams(
                CalculateUtils.dpToPx(32).toInt(),
                CalculateUtils.dpToPx(32).toInt(),
            ).apply {
                bottomMargin = CalculateUtils.dpToPx(10).toInt()
            }

            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        addView(imageView)
    }

    private fun createTextView() {
        textView = TextView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }

            maxLines = 1

            setTextColor(R.color.black4)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        }

        addView(textView)
    }
}