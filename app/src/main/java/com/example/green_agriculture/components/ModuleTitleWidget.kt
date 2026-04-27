package com.example.green_agriculture.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.green_agriculture.R

class ModuleTitleWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    lateinit var titleView: TextView

    lateinit var loadMoreView: LinearLayout

    var title: String = ""
        set(value) {
            if (value == field) return

            field = value

            titleView.text = value
        }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        createTitleView()
        createLoadMoreView()
    }

    private fun createTitleView() {
        titleView = TextView(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(R.color.black3)
        }
        addView(titleView)
    }

    private fun createLoadMoreView() {
        loadMoreView = LinearLayout(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }
            orientation = HORIZONTAL
        }

        val loadMoreText = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(R.color.black4)
            text = "更多"
        }

        val forwardIcon = IconWidget(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            iconSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                context.resources.displayMetrics
            )
            iconColor = context.getColor(R.color.primary)
            iconName = context.getString(R.string.icon_forward)
        }

        loadMoreView.addView(loadMoreText)
        loadMoreView.addView(forwardIcon)
        addView(loadMoreView)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("title")
        fun bindTitle(view: ModuleTitleWidget, title: String) {
            view.title = title
        }
    }
}